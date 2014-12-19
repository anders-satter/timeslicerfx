package se.timeslicer.test

import se.timeslicer.log.Item
import se.timeslicer.file.FileUtil
import se.timeslicer.util.Util

object Test {
  def main(args: Array[String]) {
	 val i = Item("2014-11-23 09:00", 
	     "2014-11-23 09:45",
	     "Team TDE", 
	     "scrum", 
	     "it is ok...", 
	     Util.getDayValueInMs("2014-11-23 09:00"))
	 
  }
  
  
  def parseLogItem(logLine:String):Item = {
    if (logLine.length() > 0){
    val parts = logLine.split('\t');
    //parts.map(println);
    return Item(parts(0),parts(1),parts(3),parts(4),parts(5), Util.getDayValueInMs(parts(0)))    
    }
    return null
   }
  
  
  /*
   * Try and read from the logs 
   */
  val logLines = FileUtil.readFromFile("/Users/anders/dev/eclipse_ws1/TimeslicerFX/data/log.txt")
//  println(parseLogItem(logLines(0)).start)
//  println(parseLogItem(logLines(0)).end)
// 
//  println(parseLogItem(logLines(0)).project)
//  println(parseLogItem(logLines(0)).activity)
//  println(parseLogItem(logLines(0)).duration)
//  println(parseLogItem(logLines(0)).dayValue)
//  
 
  /*
   * create a list of items
   */
  
  //logLines.map(println)
  
  val itemList = logLines.map(parseLogItem)
  itemList.map(println)
  
  
  /*
   * sort the items
   */
 
 
  /*
   * 
   */
  
  
  
  
  
  //println(logLines(0))
  //println(logLines.size)  
}