package se.timeslicer.reporting

import scala.collection.mutable.ListBuffer
import se.timeslicer.log.Item
import se.timeslicer.util.DateTime

/*
 * INTERVALRESULT
 */
class IntervalResult {
  private var _start: String = ""
  private var _end: String = ""
  private var _projectList: Iterable[Project] = Nil
  private var _totalTime: Long = 0
  private var _selection: Array[Item] = null
  private var _itemList: Array[Item] = null
  def projectList_=(list: Iterable[Project]) = _projectList = list
  def projectList = _projectList
  def totalTime = _totalTime
  def totalTime_=(time: Long) = _totalTime = time
  def selection = _selection
  def selection_=(list: Array[Item]) = _selection = list
  def start = _start
  def start_=(day: String) = _start = day
  def end_=(day: String) = _end = day
  def end = _end
  def itemList = _itemList
  def itemList_=(list: Array[Item]) = _itemList = list

  /**
   * This method is not needed right now
   */
  private def sortedGroupByDayMap: Seq[(Long, Array[Item])] = selection.groupBy(_.dayValue)
    /**
     * groupBy returns map, which is always unsorted,
     * so we need to get a sorted seq to get the days
     * in the right order
     */
    .toSeq.sortBy(_._1)

  
  /**
   * returns a map of  
   */
  private def groupByDayMap:Map[Long, Array[Item]] = selection.groupBy(_.dayValue)
    
  def present = {
    projectList.foreach(_.present(totalTime))
    println("-----------------------------------")
    println("Total time: " + DateTime.getDecimalHours(totalTime))
  }
 
 /**
  * returns a map of (dayValue:Long, sumOfDurationForTheDay:Long) items 
  */
 def daySumMap:Map[Long, Long] = {
   /*
    * get a map of items grouped on day 
    */
   val dayItemsMap = groupByDayMap
   /*
    * create a map of one sum per day
    */
   val d = dayItemsMap.map(item => {
    val sum = item._2.map(_.duration).reduceLeft(_ + _)
    (item._1 -> sum)
  })
  return d
 }
 
}/*IntervalResult*/



///*
//   * DAYSUM
//   */
//case class DaySum(dayValue: Long, day: String, sum: Double)
//
//object DaySum {
//  def createDaySum(dayValue: Long, dayItems: Array[Item]) = {
//    val sumOfDay = DateTime.getDecimalHours(dayItems.map(_.duration).reduceLeft(_ + _))
//    val dayStr = DateTime.getDayValueInStr(dayValue)
//    DaySum(dayValue, dayStr, sumOfDay)
//  }
//}