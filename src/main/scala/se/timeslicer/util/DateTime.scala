package se.timeslicer.util

import scala.math.BigDecimal
import java.util.Date
import java.util.Locale
import se.timeslicer.reporting.CalendarDay
import java.util.Calendar
import se.timeslicer.settings.Settings

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
    BigDecimal(minutes / 60.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble    
  }

  def getNumberOfDaysInInterval(start: Long, end: Long): Int = {
    ((end - start) / oneDayMs).toInt
  }

  def dayName(dayValue: Long) = {
    formatDayName.format(new Date(dayValue))
  }

  def weekDayNumber(day: Long) = {
    val c = Calendar.getInstance()
    c.setTime(new Date(day))
    c.get(Calendar.DAY_OF_WEEK)
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
  
  def getTimePart(time: String) = {
    val parsedDate = format.parse(time)
    formatTime.format(parsedDate)
    //parsedDate.getHours + ":"+ parsedDate.getMinutes
  }

  def currentDay = {
    formatDay.format(Calendar.getInstance.getTime())
  }

  def isDay(test: String): Boolean = {
    var result = false
    if (test.length() == 10) {
      try {
        formatDay.parse(test)
        result = true
      } catch {
        case e: Exception => result = false
      }
    }
    result
  }

  def dayNormalTime(day: Long): Double = {
    var result = 0.0
    val name = dayName(day)
    val formattedDay = getDayValueInStr(day)
    if (name.toLowerCase().startsWith("sat")
      || name.toLowerCase().startsWith("sun")) {
      result = 0.0
    } else if (Settings.specialWorkdays.contains(formattedDay)) {
      result = Settings.specialWorkdays(formattedDay)
    } else {
      result = Settings.propertiesMap("NormalTime").toDouble
    }
    result
  }

  def isSaturday(day: Long): Boolean = {
    weekDayNumber(day) == 7
    //dayName(day).trim().toLowerCase().startsWith("sat")
  }
  def isSaturday(day: String): Boolean = {
    isSaturday(getDayValueInMs(day))
  }

  def isSunday(day: Long): Boolean = {
    weekDayNumber(day) == 1
    //dayName(day).trim().toLowerCase().startsWith("sun")  
  }
  def isSunday(day: String): Boolean = {
    isSunday(getDayValueInMs(day))
  }

  class DayType() {
    private val dt = DateTime
    private var _currentDay: Long = 0
    def currentDay: Long = _currentDay
    def currentDay_=(value: Long): Unit = { _currentDay = value }
    def currentDay_=(value: String): Unit = { _currentDay = dt.getDayValueInMs(value) }
    def isSaturday = { dt.isSaturday(_currentDay) }
    def isSunday = { dt.isSunday(_currentDay) }
  }

  /**
   * the main function is only used for tests
   */
  def main(args: Array[String]) {
//    val dt = new DayType
//    dt.currentDay = "2015-01-03"
//    println(dt.isSaturday)
//    println(dt.isSunday)
//
//    dt.currentDay = "2015-01-04"
//    println(dt.isSaturday)
//    println(dt.isSunday)

      println(getTimePart("2015-01-04 08:36"))
    
    
//    
//    println(isSaturday("2015-01-03"))
//    println(isSaturday("2015-01-04"))
//
//    println(isSunday("2015-01-04"))
//    println(isSunday("2015-01-05"))
//
  }
}