import se.timeslicer.log.LogItem
object TestSheet {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  var item = new LogItem                          //> item  : se.timeslicer.log.LogItem = se.timeslicer.log.LogItem@1e67b872
  item.starttime = "2014-11-14 11:45"
  item.endtime = "2014-11-14 12:00"

	
  println(item.duration)                          //> 15

  val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
                                                  //> format  : java.text.SimpleDateFormat = java.text.SimpleDateFormat@ba23d43a
  val d1 = format.parse("2014-11-10 10:00")       //> d1  : java.util.Date = Mon Nov 10 10:00:00 CET 2014
  val d2 = format.parse("2014-11-10 12:45")       //> d2  : java.util.Date = Mon Nov 10 12:45:00 CET 2014

  (d2.getTime() - d1.getTime()) / 1000 / 60       //> res0: Long = 165
  
 
 
}