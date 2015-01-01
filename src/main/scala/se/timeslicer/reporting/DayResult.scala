package se.timeslicer.reporting

import java.util.Date
import se.timeslicer.util.DateTime
import scala.collection.mutable.ListBuffer
import se.timeslicer.log.Item
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import scalafx.collections.ObservableBuffer
import scalafx.beans.property.StringProperty
import se.timeslicer.util.ItemUtil

/**
 *
 */
case class CalendarDay(dayValue: Long, day: String, name: String)

/**
 * This class shows the result of one day
 */
case class DayResult(day: CalendarDay, durationMins: Long, durationHrs: Double, normalTime: Double, diffWtNt: Double, accWt: Double, accNt: Double, diffAccWtNt: Double)

/**
 * Used by the tableview in the ReportingHelper
 */
class DayResultTableRow(day_ : String, dayName_ : String, hours_ : String, normalTime_ : String, diffWtNt_ : String, accWt_ : String, accNt_ : String, diffAccWtNt_ : String) {
  val day = new StringProperty(this, "day", day_)
  val dayName = new StringProperty(this, "dayName", dayName_)
  val hours = new StringProperty(this, "hours", hours_)
  val normalTime = new StringProperty(this, "normalTime", normalTime_)
  val diffWtNt = new StringProperty(this, "diffWtNt", diffWtNt_)
  val accWt = new StringProperty(this, "accWt", accWt_)
  val accNt = new StringProperty(this, "accNt", accNt_)
  val diffAccWtNt = new StringProperty(this, "diffAccWtNt", diffAccWtNt_)
}

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
  def getDayResult(start: Long, end: Long, daySumMap: Map[Long, Long]): List[DayResult] = {

    var accumulatedWorkedTime: Double = 0.0
    var accumulatedNormalTime: Double = 0.0
    /*
     * create the calendar list
     */
    calendarDayList(start, end).map(calDay => {
      /*
       * calculate the sum of the day
       */
      var daySumMnts = 0
      var daySumHrs: Double = 0.0
      if (daySumMap.contains(calDay.dayValue)) {
        val part = daySumMap(calDay.dayValue)
        daySumMnts = part.intValue()
        daySumHrs = DateTime.getDecimalHours(part.intValue)
      }
      val normalTime: Double = DateTime.dayNormalTime(calDay.dayValue)
      val diffWtNt: Double = daySumHrs - normalTime
      accumulatedWorkedTime += daySumHrs
      accumulatedNormalTime += normalTime
      val diffAccWtNt: Double = accumulatedWorkedTime - accumulatedNormalTime
      //case class DayResult(day: CalendarDay, durationMins: Long, durationHrs: Double, diffWtNt: Double, accWt: Double, accNt: Double, diffAccWtNt: Double)
      val iu = ItemUtil
      DayResult(calDay, daySumMnts,
        daySumHrs,
        iu.round2(normalTime),
        iu.round2(diffWtNt),
        iu.round2(accumulatedWorkedTime),
        iu.round2(accumulatedNormalTime),
        iu.round2(diffAccWtNt))
    })
  }

  def dayResultTableRowBuffer(start: Long, end: Long, daySumMap: Map[Long, Long]): ObservableBuffer[DayResultTableRow] = {
    val observableBuffer: ObservableBuffer[DayResultTableRow] = new ObservableBuffer();
    val dayResult = getDayResult(start, end, daySumMap)
    dayResult.map(item => {
      observableBuffer += new DayResultTableRow(item.day.day, 
          item.day.name,
          item.durationHrs.toString, 
          item.normalTime.toString, 
          item.diffWtNt.toString, 
          item.accWt.toString, 
          item.accNt.toString, 
          item.diffAccWtNt.toString)
    })
    observableBuffer
  }

  def main(args: Array[String]): Unit = {
    calendarDayList(1383260400000L, 1383260400000L + (DateTime.oneDayMs * 10)).map(println)
    //val test = new DayResultTableRow("2012", "Fri", "1.45", "7.7")
    //println(test.day)

  }
}