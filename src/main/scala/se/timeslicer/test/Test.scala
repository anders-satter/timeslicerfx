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

/**
 * Test the different parts of the application
 */
object Test {
  def main(args: Array[String]) {
    val i = Item("2014-11-23 09:00",
      "2014-11-23 09:45",
      "Team TDE",
      "scrum",
      "it is ok...",
      DateTime.getDayValueInMs("2014-11-23 09:00"))
  }

  /*
   * load settings
   */
  Settings.loadProperties
  /*
   * Try and read from the logs 
   */
  val logLines = FileUtil.readFromFile("/Users/anders/dev/eclipse_ws1/TimeslicerFX/data/log.txt")
  /*
   * create a list of items
   */

  val interval = new IntervalResult()
  //  interval.start = "2014-12-29"
  //  interval.end = "2014-12-30"
  interval.start = "2013-11-01"
  interval.end = "2013-11-30"

  val itemList = logLines.map(ItemUtil.parseLogItem).filter(_ != null).sortBy(_.dayValue)

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

    println(interval.projectList)
    interval.projectList.foreach(_.compile)
    interval.totalTime = interval.projectList.map(_.totalTime).reduceLeft(_ + _)
    println(interval.present.toString)

    /*
   * SUMMARY OF EACH DAY
   */
    val daySumMap = interval.daySumMap
    //println(daySumMap)

    /*
   * CALENDAR
   */

    /*
   * list of dates
   */

    //val calDayList = DayResultHelper.calendarDayList(DateTime.getDayValueInMs(interval.start), DateTime.getDayValueInMs(interval.end))
    //calDayList.map(println)
   val dayResultList = DayResultHelper.getDayResult(DateTime.getDayValueInMs(interval.start), DateTime.getDayValueInMs(interval.end), daySumMap)

    dayResultList.map(println)

  } else {
    println("No items found")
  }

}