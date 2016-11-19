package com.apanfilov.testtask

import com.apanfilov.testtask.schema.FoodAnalyticsSchema
import com.apanfilov.testtask.argument.CommandLineArgumentParser
import com.apanfilov.testtask.flow.{SqlFlow, TranslateFlow}
import org.apache.commons.io.FilenameUtils
import org.apache.hadoop.yarn.util.RackResolver
import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}


object DriverClass {
  val maxDOP:Int = 10
  val maxPostTextSize:Int = 1000

  def main(args: Array[String]): Unit = {

    val inputArgs = CommandLineArgumentParser.parseArgs(args) match {
      case Some(args) => args
      case None => {
        throw new IllegalArgumentException("Please provide all necessary parameters")
      }
    }
    val workFolder = FilenameUtils.getPrefix(inputArgs.pathToData) + FilenameUtils.getPath(inputArgs.pathToData)
    // Reduce Log severity
    Logger.getLogger(classOf[RackResolver]).getLevel
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val sparkSession = SparkSession
      .builder()
      //.master("local[4]")
      .appName("FoodAnalytics")
      .config("spark.sql.warehouse.dir", workFolder)
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      //.config("spark.kryoserializer.buffer.mb","24")
      // Performance tuning should be configurated outside
      .config("spark.driver.memory", "500m")
      .config("spark.executors.memory", "500m") // in case of multiple machines it should be changed
      .config("spark.executors.cores", "3")
      .config("spark.executor.instances", "1") // in case of multiple machines it should be changed
      .enableHiveSupport()
      .getOrCreate()


    import sparkSession.implicits._

    val ds = sparkSession.read
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .load(inputArgs.pathToData)
      .as[FoodAnalyticsSchema]
    ds.persist()
    ds.createOrReplaceTempView("food_analytics")

    if(inputArgs.translate) {
      TranslateFlow.execute(sparkSession, ds, workFolder)
    }
    else {
      SqlFlow.execute(sparkSession, ds)
      /*
    val mostActiveUsersQuery =
      """
        SELECT ProfileName, COUNT(ProfileName) as cnt
        FROM food_analytics
        GROUP BY ProfileName
        ORDER BY cnt DESC
        LIMIT 1000
      """
    println("Most Active Users :")
    sparkSession.sql(mostActiveUsersQuery).collect().foreach(println)

    val mostCommentedFoodItems =
      """
        SELECT ProductId, COUNT(ProductId) as cnt
        FROM food_analytics
        GROUP BY ProductId
        ORDER BY cnt DESC
        LIMIT 1000
      """
    println("Most Commented Food Item (ProductId) :")
    sparkSession.sql(mostCommentedFoodItems).collect().foreach(println)

    // For Most Used Words I decided to use RDD instead of Dataset
    // as it more convenient in my opinion

    val wordCountRDD = ds.rdd // Convert Dataset to RDD
      .map(row => row.text)
      .flatMap(_.toLowerCase.split("\\W+"))
      .map(word => (word, 1))
      .reduceByKey(_ + _)

    println("Most Used words in the review :")
    wordCountRDD.map { case (k, v) => (v, k) }.sortByKey(false).collect().take(1000).foreach(println)
*/
    }

  }

}