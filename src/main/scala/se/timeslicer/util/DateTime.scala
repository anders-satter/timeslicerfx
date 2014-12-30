package se.timeslicer.util

import scala.math.BigDecimal
import java.util.Date
import java.util.Locale
import se.timeslicer.reporting.CalendarDay
import java.util.Calendar

/**
 * Holds the the calendar day
 */

object DateTime {
  private val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
  private val formatDay = new java.text.SimpleDateFormat("yyyy-MM-dd")
  private val formatDayName = new java.text.SimpleDateFormat("EE", Locale.ENGLISH)
  private val formatTime = new java.text.SimpleDateFormat("HH:mm")
  val oneDayMs: Long = 1000 * 60 * 60 * 24

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

 def dayName(dayValue:Long) = {
   formatDayName.format(new Date(dayValue))
 }
  
  private def incrementor(initVal: Int): () => Int = {
    var startVal: Int = initVal
    () => {
      startVal = startVal + 1
      startVal
    }
  }

  private def incrementor2(initVal: Int): Map[String, () => Int] = {
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

  def currentTime = {
    formatTime.format(Calendar.getInstance.getTime())
  }
  
  def currentDay = {
    formatDay.format(Calendar.getInstance.getTime())
  }
    /**
   * the main function is only used for tests
   */
  def main(args: Array[String]) {
    println(currentTime)
  }
}