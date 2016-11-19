package com.apanfilov.testtask.argument

import scopt.OptionParser


case class CommandLineArguments(pathToData: String = "", translate: Boolean = false)

object CommandLineArgumentParser extends OptionParser[CommandLineArguments]("FoodAnalytics") {
  head(programName)
  opt[String]("pathToData") required() action {
    (arg, arguments) => arguments.copy(pathToData = arg)
  } text("path to data file")
  opt[Unit]("translate") action {
    (_ , arguments) => arguments.copy(translate = true)
  } text("flag if deined translation will be provided")

  def parseArgs(args: Array[String]): Option[CommandLineArguments] = {
    parse(args, CommandLineArguments())
  }
}