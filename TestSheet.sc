import se.timeslicer.log.LogItem
object TestSheet {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet

  var item = new LogItem                          //> item  : se.timeslicer.log.LogItem = se.timeslicer.log.LogItem@3f2a3a5
  item.starttime = "2014-11-14 11:45"
  item.endtime = "2014-11-14 12:00"

  println(item.duration)                          //> 15

  val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
                                                  //> format  : java.text.SimpleDateFormat = java.text.SimpleDateFormat@ba23d43a
  val d1 = format.parse("2014-11-10 10:00")       //> d1  : java.util.Date = Mon Nov 10 10:00:00 CET 2014
  val d2 = format.parse("2014-11-10 12:45")       //> d2  : java.util.Date = Mon Nov 10 12:45:00 CET 2014

  (d2.getTime() - d1.getTime()) / 1000 / 60       //> res0: Long = 165

  case class Item(project: String, activity: String, start: String, end: String) {
    def time = {
      val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
      val d1 = format.parse(start)
      val d2 = format.parse(end)

      (d2.getTime() - d1.getTime()) / 1000 / 60

    }
  }

  val c = Item("Team TDE", "activity", "2014-11-13 12:00", "2014-11-13 12:00")
                                                  //> c  : TestSheet.Item = Item(Team TDE,activity,2014-11-13 12:00,2014-11-13 12:
                                                  //| 00)
  c.project                                       //> res1: String = Team TDE
  c.activity                                      //> res2: String = activity
  c.time                                          //> res3: Long = 0

}