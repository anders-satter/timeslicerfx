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
import scala.collection.mutable.ListBuffer
import se.timeslicer.reporting.Padder
/**
 * will show hours per item (project+actitivty) for a time interval
 */
object WeeklyReporting {
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
  val logLines = FileUtil.readFromFile(Settings.logFileName, Settings.propertiesMap("LogFileEncoding"))

  /*
   * make list of all dates in the interval
   */
  val startReportingDate = "2015-07-01"
  val endReportingDate = "2015-07-03"

  val interval = new IntervalResult()
  interval.start = startReportingDate
  interval.end = endReportingDate

  /*
   * sorting all item to make sure no one is missed
   */
  interval.itemList = logLines.map(ItemUtil.parseLogItem).filter(_ != null).sortBy(_.dayValue)
  /*
   * get items in specified interval
   */
  interval.selection = ItemUtil.itemsInInterval(interval.itemList, interval.start, interval.end)

  /*
   * key prj+act, list of time items sorted by prj+act
   * and denoted as Project|Activity
   */
  val projectActivityMap = interval.selection.toList.sortBy(item => {
    item.project + item.activity
  }).groupBy(item => {
    item.project + "|" + item.activity
  })

  case class PrjActSumForDay(name: String, date: String, dateValue: Long, sum: String)

  val prjList = new ListBuffer[PrjActSumForDay]
  val dt = DateTime
  //  prjActMap.foreach(item => {
  //
  //    val entries = item._2
  //    val actByDay = entries.sortBy(_.dayValue).groupBy(_.dayValue)
  //    actByDay.foreach(i => {
  //      /*
  //       * list same prj+act on same day
  //       * so need to summarize these items 
  //       */
  //      var sumPerDay = 0.0
  //      val exposedDay = ""
  //      val sumHours = i._2.map(item2 => {
  //        //1 calc the sum of hours
  //        //val mins= dt.elapsedMinutes(dt.getTimePart(item2.start),dt.getTimePart(item2.end))
  //        val mins = dt.elapsedMinutes(item2.start, item2.end)
  //        val hours = dt.getDecimalHours(mins)
  //        sumPerDay += hours
  //      })
  //      prjList += PrjActSumForDay(item._1, dt.getDayValueInStr(i._1), i._1, String.valueOf(sumPerDay))
  //    })
  //  })

  
  /*
   * Project|Activity
   *  Item(Project|Activity,day1)
   *  Item(Project|Activity,day2)
   *  Item(Project|Activity,day3)
   *  etc
   */
  projectActivityMap.foreach(item => {

    /*
     * Entries for each Project|Activity
     */
    val entries = item._2
    
    /*
     * DayValue
     *  Item(Project|Activity, day1)
     *  Item(Project|Activity, day1)
     *  so if we have more than one item for each
     *  key, this means that we have multiple items of the
     *  same kind for each day, the need to be summarized
     */
    val activitiesForEachDay = entries.sortBy(_.dayValue).groupBy(_.dayValue)
    val list = activitiesForEachDay.map(i => {
      /*
       * list same prj+act on same day
       * so need to summarize these items 
       */
      //var sumPerDay = 0.0
      val exposedDay = ""
      val sumHours = i._2.map(item2 => { 
        dt.getDecimalHours(dt.elapsedMinutes(item2.start, item2.end))       
      }).foldLeft(0.0)(_ + _)
      //PrjActSumForDay(item._1, dt.getDayValueInStr(i._1), i._1, String.valueOf(sumPerDay))
      prjList += PrjActSumForDay(item._1, dt.getDayValueInStr(i._1), i._1, String.valueOf(sumHours))
    })
  })

  //is in day span
  
  /*
   * Produce a list of days for the report
   */
  val start = dt.getDayValueInMs(startReportingDate)
  val end = dt.getDayValueInMs(endReportingDate)
  val dateList = dt.getDayList(startReportingDate, endReportingDate)
  
  /*
   * remove lunch
   */
  val itemNames = prjList.filter(_.name != "Annat|Lunch").sortBy(_.name).groupBy(_.name)

  val RUN_MEW = true

  if (RUN_MEW) {
    /*
    * jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj
    */

    //itemNames.map(println)
    //setting the matrix size with respect to titles and sums (+2)
    val matrix = Array.ofDim[String](itemNames.keys.size + 2, dateList.length + 2)

    matrix(0)(0) = Padder.padd("Code", 50)

    /*
     * setting the dates 
     */
    var index = 1
    dateList.foreach(d => {
      matrix(0)(index) = Padder.padd(d, 10)
      index += 1
    })

    /*
     * setting the items
     */
    var rowNum = 1
    itemNames.foreach(i => {
      matrix(rowNum)(0) = Padder.padd(i._1, 50)
      rowNum += 1
    })
    matrix(rowNum)(0) = Padder.padd("SUM", 50)

    /*
     * run through the list of dates, if we find a
     */
    var colNum = 1
    dateList.foreach(i => {

      /*
       * print the names in the first column 
       */
      var row = 1
      itemNames.foreach(j => {
        var hasValue = false
        //itemsInRange.foreach(x => {
        prjList.foreach(x => {
          if (x.name == j._1 && x.date == i) {
            matrix(row)(colNum) = Padder.padd(x.sum, 10)
            hasValue = true
          }

        })
        if (!hasValue) {
          matrix(row)(colNum) = Padder.padd("0.0", 10)
        }
        row += 1
      })
      colNum += 1
    })

    /*
     * SET THE SUMS OF FROM THE MATRIX
     */
    val dateListLen = dateList.length;
    val itemNameLen = itemNames.keys.size
    val sumRow = itemNameLen + 1
    val sumCol = dateListLen + 1

    /*
     * day sums
     */
    for (i <- 1 to dateListLen) {
      var currentSum = 0.0
      for (j <- 1 to itemNameLen) {
        currentSum += matrix(j)(i).toDouble
      }
      matrix(sumRow)(i) = Padder.padd(String.valueOf(currentSum), 10)
    }

    /*
     * prj|act sums 
     */
    matrix(0)(sumCol) = Padder.padd("SUM", 10)

    for (i <- 1 to itemNameLen) {
      var currentSum = 0.0
      for (j <- 1 to dateListLen) {
        currentSum += matrix(i)(j).toDouble
      }
      matrix(i)(sumCol) = Padder.padd(String.valueOf(currentSum), 10)
    }

    /*
     * calculate the sum of sums
     */

    // 1. sum of prj|act
    var prjActPeriodSum = 0.0
    for (i <- 1 to itemNameLen) {
      prjActPeriodSum += matrix(i)(sumCol).toDouble
    }

    // 2. sum of day
    var daySum = 0.0
    for (i <- 1 to dateListLen) {
      daySum += matrix(sumRow)(i).toDouble
    }

    println("Diff days - code sums: " + (daySum - prjActPeriodSum))

    matrix(sumRow)(sumCol) = String.valueOf(prjActPeriodSum)

    /*
     * print the stuff
     */
    val nSz = itemNames.keys.size + 1
    val dateSz = dateList.length + 1

    for (i <- 0 to nSz) {
      for (j <- 0 to dateSz) {
        print(matrix(i)(j) + " ")
      }
      println("")
    }

  } else {
    /*
    * jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj
    * 
    *  
    */

    dateList.foreach(i => {
      println(i)
      itemNames.foreach(j => {
        print(j._1 + " ")
        var hasValue = false;
        //itemsInRange.foreach(x => {
        prjList.foreach(x => {
          if (x.name == j._1 && x.date == i) {
            println(x.sum)
            hasValue = true
          }
        })
        if (!hasValue) {
          println("0.0")
        }
      })
      println("")
    })

    /*
   * kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk
   */

  }

}