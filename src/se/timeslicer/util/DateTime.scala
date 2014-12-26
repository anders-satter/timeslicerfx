package se.timeslicer.util

import scala.math.BigDecimal
import java.util.Date
import java.util.Locale

/**
 * Holds the the calendar day
 */
case class CalendarDay(dayValue: Long, day: String, name: String)


object DateTime {
  private val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
  private val formatDay = new java.text.SimpleDateFormat("yyyy-MM-dd")
  private val formatDayName = new java.text.SimpleDateFormat("EE", Locale.ENGLISH)
  private val oneDayMs: Long = 1000 * 60 * 60 * 24
  
  
  def elapsedMinutes(start: String, end: String) = {
    val d1 = format.parse(start)
    val d2 = format.parse(end)
    (d2.getTime() - d1.getTime()) / 1000 / 60
  }

  def getDayValueInMs(day: String): Long = {
    formatDay.parse(day).getTime()
  }

  def getDayValueInStr(day: Long): String = {
    formatDay.format(new java.util.Date(day))
  }

  def getDecimalHours(minutes: Long): Double = {
    return BigDecimal(minutes / 60.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  def getNumberOfDaysInInterval(start: Long, end: Long): Int = {
    ((end - start) / oneDayMs).toInt
  }

  def incrementor(initVal: Int): () => Int = {
    var startVal: Int = initVal
    () => {
      startVal = startVal + 1
      startVal
    }
  }

  def incrementor2(initVal: Int): Map[String, () => Int] = {
    var startVal: Int = initVal
     return Map(
      ("next", () => {
        startVal = startVal + 1
        startVal
      }),
      ("reset", () => {
        startVal = initVal
        startVal
      }))
  }

  def nextDay = (day: Long) => day + oneDayMs

  def dayIncrementor(startDay:Long):() => Long = {
    var currentDay = startDay - oneDayMs
    return ()=> {
      currentDay = currentDay + oneDayMs 
      currentDay
    }
  }
  
  def calendarDayList(start:Long, end:Long):List[CalendarDay] = {
    val dInc = dayIncrementor(start);
    (0 to getNumberOfDaysInInterval(start, end)).map(
        i=> dInc()).map(
            x => CalendarDay(x, DateTime.getDayValueInStr(x),formatDayName.format(new Date(x)))).toList
  }
  
  def testFunction:Int = {
    return 1
  }
  /**
   * the main function is only used for tests
   */
  def main(args: Array[String]) {
    calendarDayList(1383260400000L, 1383260400000L+(oneDayMs*10)).map(println)
    println(testFunction)
  }
}