package se.timeslicer.util

import se.timeslicer.log.Item

object ItemUtil {
  def parseLogItem(logLine:String):Item = {
    if (logLine.length() > 0){
    	val parts = logLine.split('\t');
    	//println(logLine)
    	return Item(parts(0),
    	    parts(1),
    	    parts(3).replaceAll("\"", ""), 
    	    parts(4).replaceAll("\"", ""),
    	    parts(5).replaceAll("\"", ""), 
    	    DateTime.getDayValueInMs(parts(0)))    
    }
    return null
   }
  
  /**
   * outputs entries between start (inclusive) and end (inclusive)
   */
  def itemsInInterval(list:Array[Item], start:String, end:String):Array[Item] = {
    list.filter(item => item.dayValue>=DateTime.getDayValueInMs(start)  &&
        item.dayValue <= DateTime.getDayValueInMs(end) 
    )
  }

  def round(value:Double, decimals:Int):Double= {
    return BigDecimal(value).setScale(decimals, BigDecimal.RoundingMode.HALF_UP).toDouble
  }
  def round2(value:Double) = round(value, 2);
  
  def percent(x:Long, y:Long, decimals:Int):Double = {
    if (x != 0 && y != 0){
      return round(x.toDouble/y.toDouble * 100, decimals)  
    } else {
      return 0.0
    } 
    
  }
  
  def removeCitationMarks(name:String):String = {
    name.replaceAll("\"", "")
  }
  
}