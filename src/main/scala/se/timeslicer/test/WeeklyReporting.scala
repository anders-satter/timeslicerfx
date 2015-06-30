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

  val interval = new IntervalResult()
  interval.start = startReportingDate
  interval.end = endReportingDate

  /*
    *  convert the list to items
    */
  interval.itemList = itemList
  interval.selection = ItemUtil.itemsInInterval(interval.itemList, interval.start, interval.end)
  //interval.selection.foreach(item=> println(item))
  val sortedItems = interval.selection.toList.sortBy(item => item.project)
  val list = interval.selection.toList

  //key prj+act, list of items sorted by prj+act
  val prjActMap = list.sortBy(item => {
    item.project + item.activity
  }).groupBy(item => {
    item.project + "|" + item.activity
  })
  //val sortedList = list.sortBy( x => (x.activity) ).foreach(a => println(a) )

  case class PrjActSumForDay(name: String, date: String, dateValue: Long, sum: String)

  val prjList = new ListBuffer[PrjActSumForDay]

  val dt = DateTime
  prjActMap.foreach(item => {
    //println(item._1)
    val entries = item._2
    val actByDay = entries.sortBy(_.dayValue).groupBy(_.dayValue)
    actByDay.foreach(i => {
      //println(dt.getDayValueInStr(i._1))      
      //list same prj+act on same day
      //i._2.map(println)
      //so need to summarize these items 
      var sumPerDay = 0.0
      val exposedDay = ""
      val sumHours = i._2.map(item2 => {
        //1 calc the sum of hours
        //val mins= dt.elapsedMinutes(dt.getTimePart(item2.start),dt.getTimePart(item2.end))
        val mins = dt.elapsedMinutes(item2.start, item2.end)
        val hours = dt.getDecimalHours(mins)
        sumPerDay += hours
      })
      prjList += PrjActSumForDay(item._1, dt.getDayValueInStr(i._1), i._1, String.valueOf(sumPerDay))
    })
  })

  //prjList.map(println)

  //find all items for one day

  val startDate = "2015-06-01"
  val endDate = "2015-06-07"

  //is in day span
  val start = dt.getDayValueInMs(startDate)
  val end = dt.getDayValueInMs(endDate)
  
  val itemsInRange = prjList.filter(i => i.dateValue >= start && i.dateValue <= end && i.name != "Annat|Lunch")

  val dateList = dt.getDayList(startDate, endDate)
  val itemNames = itemsInRange.sortBy(_.name).groupBy(_.name)

  val RUN_MEW = true

  if (RUN_MEW) {
    /*
   * jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj
   */

    //itemNames.map(println)
    val matrix = Array.ofDim[String](itemNames.keys.size + 2, dateList.length + 1)

    matrix(0)(0) = Padder.padd("Code", 50)

    //setting the dates
    var index = 1
    dateList.foreach(d => {
      matrix(0)(index) = Padder.padd(d,10)
      index += 1
    })

    //setting the items
    var rowNum = 1
    itemNames.foreach(i => {
      matrix(rowNum)(0) = Padder.padd(i._1, 50)
      rowNum += 1
    })
    matrix(rowNum)(0) = Padder.padd("SUM", 50)

    //run through the list of dates, if we find a
    var colNum = 1
    dateList.foreach(i => {
      //println(i)

      //print the names in the first column
      
      var row = 1
      itemNames.foreach(j => {
        var hasValue = false
        itemsInRange.foreach(x => {
          if (x.name == j._1 && x.date == i) {
            matrix(row)(colNum) = Padder.padd(x.sum,10)
            hasValue = true
          }
          
        })
        if(!hasValue){
          matrix(row)(colNum) = Padder.padd("0.0",10)
        }
        row += 1
      })
      colNum += 1
    })

    
    //set the sums
    
    
    
    
    val nSz = itemNames.keys.size+1
    val dateSz = dateList.length

    for (i <- 0 to nSz) {
      for (j <- 0 to dateSz) {
        print(matrix(i)(j) + " ")
      }
      println("")
    }

  } else {
    /*
   * jjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj
   */

    dateList.foreach(i => {
      println(i)
      itemNames.foreach(j => {
        print(j._1 + " ")
        var hasValue = false;
        itemsInRange.foreach(x => {
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

  /*
   * try to present all this in a matrix
   * rows colums
   */

  //sortedList.map(println)

  //    if (interval.selection.length > 0) {
  //      /*
  //      * map.key = project, value = array of activities
  //      */
  //      val byProject = interval.selection.groupBy(_.project)
  //
  //      interval.projectList = byProject.map(entry => {
  //        new Project(entry._1, entry._2)
  //      })
  //
  //      interval.projectList.foreach(_.compile)
  //      interval.totalTime = interval.projectList.map(_.totalTime).reduceLeft(_ + _)
  //
  //      /*
  //      *SUMMARY OF EACH DAY
  //      */
  //      val daySumMap = interval.daySumMap
  //
  //      val dayResultList = DayResultHelper.getDayResult(DateTime.getDayValueInMs(interval.start), DateTime.getDayValueInMs(interval.end), daySumMap)
  //      /*
  //     * print the day
  //     */
  //      println(interval.start + " " +
  //        DateTime.dayName(DateTime.getDayValueInMs(interval.start)) + " " +
  //        DateTime.getDecimalHours(interval.totalTime))
  //
  //      println(interval.presentLinear)
  //    } else {
  //      /*
  //     * print the day
  //     */
  //      println(interval.start + " " +
  //        DateTime.dayName(DateTime.getDayValueInMs(interval.start)) + " " +
  //        DateTime.getDecimalHours(interval.totalTime))
  //
  //      println("No items found")
  //      //extra line feed for the printing
  //      println("")
  //
  //    }

}