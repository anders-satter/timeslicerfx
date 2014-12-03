import se.timeslicer.log.LogItem
object TestSheet {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(95); 
  println("Welcome to the Scala worksheet");$skip(26); 

  var item = new LogItem;System.out.println("""item  : se.timeslicer.log.LogItem = """ + $show(item ));$skip(38); 
  item.starttime = "2014-11-14 11:45";$skip(36); 
  item.endtime = "2014-11-14 12:00";$skip(28); 

	
  println(item.duration);$skip(67); 

  val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");System.out.println("""format  : java.text.SimpleDateFormat = """ + $show(format ));$skip(44); 
  val d1 = format.parse("2014-11-10 10:00");System.out.println("""d1  : java.util.Date = """ + $show(d1 ));$skip(44); 
  val d2 = format.parse("2014-11-10 12:45");System.out.println("""d2  : java.util.Date = """ + $show(d2 ));$skip(45); val res$0 = 

  (d2.getTime() - d1.getTime()) / 1000 / 60;System.out.println("""res0: Long = """ + $show(res$0))}
  
 
 
}
