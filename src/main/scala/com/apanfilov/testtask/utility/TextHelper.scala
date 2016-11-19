package com.apanfilov.testtask.utility

/**
  * Helper object to hold text functions
  */
object TextHelper {
  /**
    * Function injects the key from tuple into the String value with pattern {id_sentenceNo}value.
    * Returns a function
  */
  def injectReviewId: ((Int, (String, Int))) => (String) = {
    case (id:Int, (sentence:String, sentenceNo:Int)) => {
      "{" + id.toString + "_" + sentenceNo.toString + "}"+sentence
    }
  }

  /**
    * Function parses the key from a String with pattern {id_sentenceNo}value to tuple (id, value).
    * Returns a function
    */
  def pasteReviewTogether: (String) => (Int, String) = {
    (text:String) => {
      (1, "ABC")
    }

  }
  /**
    * Function splits the text into sentences.
    * Returns a function
    */
  def splitBySentence: (String) => List[(String, Int)] = {
    (text:String) => {
      text
        .split("(?<=[0\\.\\!\\?])")
        .map(_.trim)
        .zipWithIndex
        .toList
    }
  }
}
