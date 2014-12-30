package se.timeslicer.log

import java.util.Calendar
import java.util.Date

/**
 * LogItem class
 */
class LogItem {
  private val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
  private var _starttime: String = ""
  private var _endtime: String = ""
  private var _project: String = ""
  private var _activity: String = ""
  private var _comment: String = ""
  def starttime = _starttime
  def endtime = _endtime
  def project = _project
  def activity = _activity
  def comment = _comment
  def starttime_=(value: String): Unit = { _starttime = value }
  def endtime_=(value: String): Unit = _endtime = value
  def project_=(value: String): Unit = _project = value
  def activity_=(value: String): Unit = _activity = value
  def comment_=(value: String): Unit = _comment = value

  /**
   * this calculates the duration in minutes of the
   * logitem in question.
   */
  def duration: Long = {
    return (format.parse(this.endtime).getTime()-format.parse(this.starttime).getTime())/1000/60
  }
  def test:Long = {
    val format = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
    val d1 = format.parse("2014-11-10 10:00")
    val d2 = format.parse("2014-11-10 10:45")
    
    return d2.getTime() - d1.getTime()/10000/3600
  }
}
  