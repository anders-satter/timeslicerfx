package se.timeslicer.reporting

import java.util.Date
import se.timeslicer.util.DateTime
import scala.collection.mutable.ListBuffer
import se.timeslicer.log.Item


/**
 * 
 */
case class CalendarDay(dayValue: Long, day: String, name: String)

/**
 * This class shows the result of one day
 */
case class DayResult(day: CalendarDay, durationMins: Long, durationHrs: Double)

/**
 * Holds the result for one day
 * Not sure if the companion class can be modified
 * as this one is
 */
object DayResultHelper {
  private def dayIncrementor(startDay: Long): () => Long = {
    var currentDay = startDay - DateTime.oneDayMs
    return () => {
      currentDay = currentDay + DateTime.oneDayMs
      currentDay
    }
  }

  def calendarDayList(start: Long, end: Long): List[CalendarDay] = {
    val dInc = dayIncrementor(start);
    (0 to DateTime.getNumberOfDaysInInterval(start, end)).map(
      i => dInc()).map(
        x => CalendarDay(x, DateTime.getDayValueInStr(x), DateTime.dayName(x))).toList
  }

  /**
   * Merges the DaySum list (Seq) with the list of calendar dates
   * so we can see the amount of working hours per day
   */
  def getDayResult(start:Long, end:Long, daySumMap:Map[Long, Long]):List[DayResult] ={
    /*
     * create the calendar list
     */
    //var dayResultList:ListBuffer[DayResult] = new ListBuffer();
    calendarDayList(start, end).map(item => {
      /*
       * calculate the sum of the day
       */
      var daySumMnts = 0
      var daySumHrs = 0
      if (daySumMap.contains(item.dayValue)){
        val part = daySumMap(item.dayValue)
        daySumMnts = part.intValue()
        daySumHrs = part.intValue()        
      }
      DayResult(item,daySumMnts, DateTime.getDecimalHours(daySumHrs))
    }) 
  }
  def main(args: Array[String]): Unit = {
    calendarDayList(1383260400000L, 1383260400000L + (DateTime.oneDayMs * 10)).map(println)
  }
}