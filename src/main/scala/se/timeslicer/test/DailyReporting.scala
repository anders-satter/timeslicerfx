package se.timeslicer.test

import se.timeslicer.file.FileUtil
import se.timeslicer.log.Item
import se.timeslicer.reporting.Activity
import se.timeslicer.reporting.DayResultHelper
import se.timeslicer.reporting.IntervalResult
import se.timeslicer.reporting.Project
import se.timeslicer.util.DateTime
import se.timeslicer.util.ItemUtil
import se.timeslicer.settings.Settings
import se.timeslicer.input.InputHelper

object DailyReporting {
  def main(args: Array[String]) {

    //println("hello")

  }

  /*
   * load settings
   */
  Settings.loadProperties
  /*
   * Try and read from the logs 
   */
  //val logLines = FileUtil.readFromFile("data/log.txt", Settings.propertiesMap("ProjectFileEncoding"))
  val logLines = FileUtil.readFromFile(Settings.logFileName, Settings.propertiesMap("LogFileEncoding"))

  /*
   * sorting all item to make sure no one is missed
   */
  val itemList = logLines.map(ItemUtil.parseLogItem).filter(_ != null).sortBy(_.dayValue)

  /*
   * make list of all dates in the interval
   */
  val startReportingDate = "2015-06-01"
  val endReportingDate = "2015-06-30"

  /*
   * we need to create a list of dates
   */
  val listDate = DateTime.getDayList(startReportingDate, endReportingDate)

  listDate.foreach(day => {

    val interval = new IntervalResult()
    interval.start = day
    interval.end = day

    /*
    *  convert the list to items
    */
    interval.itemList = itemList
    interval.selection = ItemUtil.itemsInInterval(interval.itemList, interval.start, interval.end)

    if (interval.selection.length > 0) {
      /*
      * map.key = project, value = array of activities
      */
      val byProject = interval.selection.groupBy(_.project)

      interval.projectList = byProject.map(entry => {
        new Project(entry._1, entry._2)
      })

      interval.projectList.foreach(_.compile)
      interval.totalTime = interval.projectList.map(_.totalTime).reduceLeft(_ + _)

      /*
      *SUMMARY OF EACH DAY
      */
      val daySumMap = interval.daySumMap

      val dayResultList = DayResultHelper.getDayResult(DateTime.getDayValueInMs(interval.start), DateTime.getDayValueInMs(interval.end), daySumMap)
      /*
     * print the day
     */
      println(interval.start + " " +
        DateTime.dayName(DateTime.getDayValueInMs(interval.start)) + " " +
        DateTime.getDecimalHours(interval.totalTime))

      println(interval.presentLinear)
    } else {
      /*
     * print the day
     */
      println(interval.start + " " +
        DateTime.dayName(DateTime.getDayValueInMs(interval.start)) + " " +
        DateTime.getDecimalHours(interval.totalTime))

      println("No items found")
      //extra line feed for the printing
      println("")

    }
  })

}