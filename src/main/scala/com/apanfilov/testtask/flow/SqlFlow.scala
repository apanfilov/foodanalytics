package com.apanfilov.testtask.flow

import com.apanfilov.testtask.schema.FoodAnalyticsSchema
import org.apache.spark.sql.{Dataset, SparkSession}


object SqlFlow {
  def execute(sparkSession: SparkSession, ds: Dataset[FoodAnalyticsSchema]): Unit = {

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
  }

}
