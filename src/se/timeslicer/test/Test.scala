package se.timeslicer.test

import se.timeslicer.log.Item
import se.timeslicer.file.FileUtil
import se.timeslicer.util.DateTime
import se.timeslicer.util.ItemUtil
import se.timeslicer.reporting.Activity
import se.timeslicer.reporting.Project
import se.timeslicer.reporting.IntervalResult

/**
 * Test the different parts of the application
 */
object Test {
  def main(args: Array[String]) {
	 val i = Item("2014-11-23 09:00", 
	     "2014-11-23 09:45",
	     "Team TDE", 
	     "scrum", 
	     "it is ok...", 
	     DateTime.getDayValueInMs("2014-11-23 09:00"))
  }
  
  /*
   * Try and read from the logs 
   */
  val logLines = FileUtil.readFromFile("/Users/anders/dev/eclipse_ws1/TimeslicerFX/data/log.txt")
   /*
   * create a list of items
   */
  
  val interval = new IntervalResult()
  interval.start = "2013-11-01"
  interval.end = "2013-11-07" 
 
  interval.itemList = logLines.map(ItemUtil.parseLogItem).filter(_ != null).sortBy(_.dayValue)
  interval.selection = ItemUtil.itemsInInterval(interval.itemList, interval.start, interval.end) 
  
  /*
   * map.key = project, value = array of activities
   */
  val byProject = interval.selection.groupBy(_.project)
  
  interval.projectList = byProject.map(entry => {
	 new Project(entry._1, entry._2) 
  })
  
  interval.projectList.foreach(_.compile)
  interval.totalTime = interval.projectList.map(_.totalTime).reduceLeft(_ + _)
  interval.present
 }