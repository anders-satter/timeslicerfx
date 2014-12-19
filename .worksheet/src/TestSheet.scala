import se.timeslicer.log.LogItem
object TestSheet {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(95); 
  println("Welcome to the Scala worksheet");$skip(26); 

  var item = new LogItem;System.out.println("""item  : se.timeslicer.log.LogItem = """ + $show(item ));$skip(38); 
  item.starttime = "2014-11-14 11:45";$skip(36); 
  item.endtime = "2014-11-14 12:00";$skip(26); 

  println(item.duration);$skip(67); 

  val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");System.out.println("""format  : java.text.SimpleDateFormat = """ + $show(format ));$skip(44); 
  val d1 = format.parse("2014-11-10 10:00");System.out.println("""d1  : java.util.Date = """ + $show(d1 ));$skip(44); 
  val d2 = format.parse("2014-11-10 12:45");System.out.println("""d2  : java.util.Date = """ + $show(d2 ));$skip(45); val res$0 = 

  (d2.getTime() - d1.getTime()) / 1000 / 60

  case class Item(project: String, activity: String, start: String, end: String) {
    def time = {
      val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
      val d1 = format.parse(start)
      val d2 = format.parse(end)

      (d2.getTime() - d1.getTime()) / 1000 / 60

    }
  };System.out.println("""res0: Long = """ + $show(res$0));$skip(379); 

  val c = Item("Team TDE", "activity", "2014-11-13 12:00", "2014-11-13 12:00");System.out.println("""c  : TestSheet.Item = """ + $show(c ));$skip(12); val res$1 = 
  c.project;System.out.println("""res1: String = """ + $show(res$1));$skip(13); val res$2 = 
  c.activity;System.out.println("""res2: String = """ + $show(res$2));$skip(9); val res$3 = 
  c.time;System.out.println("""res3: Long = """ + $show(res$3))}

}
