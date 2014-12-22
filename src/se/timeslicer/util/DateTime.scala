package se.timeslicer.util

import scala.math.BigDecimal

object DateTime {
  private val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
  private val formatDay = new java.text.SimpleDateFormat("yyyy-MM-dd")
  
  def elapsedMinutes(start: String, end: String) = {
    val d1 = format.parse(start)
    val d2 = format.parse(end)
    (d2.getTime() - d1.getTime()) / 1000 / 60
  }
  
  def getDayValueInMs(day:String):Long = {
    formatDay.parse(day).getTime()
  }
  
  def getDecimalHours(minutes:Long):Double = {
    return BigDecimal(minutes/60.0).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }
  
}