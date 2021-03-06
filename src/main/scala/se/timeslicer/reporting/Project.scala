package se.timeslicer.reporting

import se.timeslicer.log.Item
import scala.collection.mutable.ListBuffer
import se.timeslicer.util.DateTime
import se.timeslicer.util.ItemUtil

/**
 * class to hold the sum of one activity
 */
class Activity(val name:String, val totalTime:Long)

/**
 * Holds a project
 */
class Project(val name: String, val items: Array[Item]) {
  private var _totalTime:Long = 0
  private var activities:ListBuffer[Activity] = new ListBuffer()
  def totalTime = _totalTime 
  def compile = {
    /*
     * Calculates the total time of the project
     * 
     * this is the way to summarize attributes of a class
     * or structure
     * - items is the list of classes
     * - map(_.duration) returns an immutable map of results from the duration calculations
     * - reduceLeft(_ + _) summarizes the values by reducing
     */
	_totalTime = items.map(_.duration).reduceLeft(_ + _);
	
	/*
	 * summarize the items for each  
	 */
	/*
	 * group the items by activity
	 */
	val activityMap = items.sortBy(_.activity).groupBy(_.activity) 
	activityMap.foreach(item =>{
	  /*
	   * summarize duration for each activity and attach
	   * a new Activity to the list, with name and duration
	   */
	  val sum = item._2.map(_.duration).reduceLeft(_ + _)
	  
	  this.activities += new Activity(item._1, sum)
	})
  }
  
  def present(totalTimeInInterval:Long, strBuffer:StringBuilder) = {
    val indent2 = "  ";
    //First write the project...
    strBuffer.append(this.name + " " + 
        DateTime.getDecimalHours(this.totalTime) +" (" + 
        ItemUtil.percent(this.totalTime,totalTimeInInterval, 2)+ "%)" + '\n')
    //...then for each activity, indent ad write the activities
    this.activities.foreach(a => {
      strBuffer.append(indent2 + a.name +" " + 
          DateTime.getDecimalHours(a.totalTime)+ " (" +
          ItemUtil.percent(a.totalTime,this.totalTime, 2) + "%)"  + '\n')
    })
  }
  
  def presentLinear(totTimeInterval:Long, strBuffer:StringBuilder) = {
    this.activities.foreach(activity => {
       strBuffer.append(Padder.padd(this.name, 30) + " " + Padder.padd(activity.name, 30) + " " +Padder.padd(String.valueOf(DateTime.getDecimalHours(activity.totalTime)),6) + '\n')
    })
  }
}
