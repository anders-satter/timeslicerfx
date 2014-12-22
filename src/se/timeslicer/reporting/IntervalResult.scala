package se.timeslicer.reporting

import scala.collection.mutable.ListBuffer
import se.timeslicer.log.Item
import se.timeslicer.util.DateTime

class IntervalResult {
  private var _start: String = ""
  private var _end: String = ""
  private var _projectList: Iterable[Project] = Nil
  private var _totalTime:Long = 0
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

  def present = {
    projectList.foreach(_.present(totalTime))
    println("-----------------------------------")
    println("Total time: " + DateTime.getDecimalHours(totalTime))
  }

}