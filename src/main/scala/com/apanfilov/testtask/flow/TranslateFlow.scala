package com.apanfilov.testtask.flow

import com.apanfilov.testtask.schema.FoodAnalyticsSchema
import com.apanfilov.testtask.utility.TextHelper
import org.apache.spark.sql.{Dataset, SparkSession}
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._

import scala.reflect.io.Path


  object TranslateFlow {

    def execute(sparkSession: SparkSession, ds: Dataset[FoodAnalyticsSchema], path: String): Unit = {
      val webApiRDD = ds.rdd
        .map(row => (row.id.toInt, row.text)) // get rdd of (id, text)
        .flatMapValues(TextHelper.splitBySentence)
        .map(TextHelper.injectReviewId)
      .repartition(8)

      val resRDD = webApiRDD
        .mapPartitions(splitPartitionsToChunks)
        .map(renderJson("en", "fr"))
      resRDD.repartition(1).saveAsTextFile(path + "/Output")
      //resRDD.foreach(mockedRequest)
    }

    private def splitPartitionsToChunks(partition: Iterator[String]): Iterator[String] = {
      {
        var buf: String = "" // current String buffer
        var accumulator: List[String] = List() // result List

        for (currentSentence <- partition) {
          if (currentSentence.length <= 1000) // if current sentence larger than 1000 characters skip for now
          //accumulator = accumulator ::: x.grouped(1000).toList
          {
            if (buf.length + currentSentence.length > 1000) {
              accumulator = buf :: accumulator
              buf = currentSentence
            }
            else {
              buf += currentSentence
            }
          }
        }
        accumulator.iterator
      }
    }

    def renderJson(input_lang: String, output_lang: String): (String) => (String) = {
      (text: String) => {
        val json = ("input_lang" -> input_lang) ~
          ("output_lang" -> output_lang) ~
          ("text" -> text)

        compact(render(json))
      }
    }

    def mockedRequest: (String) => Unit = {
      (request: String) =>
        println("Sending a request :")
        print(request)
    }

  }


