package se.timeslicer.input

import scala.collection.mutable.ListBuffer
import se.timeslicer.file.FileUtil
import scalafx.collections.ObservableBuffer
import se.timeslicer.ui.conversions.Conversion
import se.timeslicer.settings.Settings
import se.timeslicer.util.ItemUtil
import se.timeslicer.util.DateTime
import se.timeslicer.log.Item

case class InputProject(name: String, activities: ListBuffer[InputActivity])
case class InputActivity(name: String)

/**
 * Helper class to manage the input of log items,
 * projects and activities
 */
object InputHelper {
  val dt = DateTime  
  private var _currentProjectBuffer: ListBuffer[InputProject] = null
  def currentProjectBuffer = _currentProjectBuffer
  def currentProjectBuffer_=(value: ListBuffer[InputProject]): Unit = _currentProjectBuffer = value

  /**
   * Reading the projects and activities
   * from the specified file and putting
   * them into an array of strings
   */
  private def readProjectsFromFile: Array[String] = {
    FileUtil.readFromFile(Settings.projectFileName, Settings.propertiesMap("ProjectFileEncoding"))
  }

  def loadProjects: ListBuffer[InputProject] = {
    val fileLines = readProjectsFromFile
    var projectList: ListBuffer[InputProject] = new ListBuffer()
    var currentProjectName = ""
    var currentActivityList: ListBuffer[InputActivity] = new ListBuffer()
    fileLines.map(item => {
      /*
       * odd thing makes us needing to use contains instead of startsWith
       * since for the first item in the file (#Administration in the test
       * case) position 0 is empty. Oddly this is not visible in an editor 
       */
      if (item.contains("#")) {
        val bracketsPos = item.indexOf("#")
        if (currentProjectName != "") {
          projectList += new InputProject(currentProjectName, currentActivityList)
        }
        /*
         * this is a project
         */
        currentProjectName = item.substring(bracketsPos + 1, item.length())
        currentActivityList = new ListBuffer()
      } else if (item.contains("+")) {
        /*
         * this is an activity, just add it to the current activity list
         */
        currentActivityList += new InputActivity(item.substring(1, item.length()))
      }
    })
    /*
     * the last project has not been inserted, so lets do it now
     x*/
    if (currentProjectName != "") {
      projectList += new InputProject(currentProjectName, currentActivityList)
    }

    currentProjectBuffer = projectList
    return projectList
  }

  /**
   * Appending the log item to the log file
   * log.txt
   */
  def saveLogItemToFile(logItem: String) = {
    FileUtil.saveToFile(Settings.logFileName, logItem, true)
  }

  def getActivitiesForProject(projectName: String): ObservableBuffer[String] = {
    var activityList: ObservableBuffer[String] = null
    currentProjectBuffer.foreach(item => {
      if (item.name == projectName) {
        activityList = Conversion.getObservableBuffer(item.activities.sortBy(_.name.toLowerCase()).map(_.name).toSeq)
      }
    })
    return activityList
  }

  def addProject(projectName: String) = {
    /*
     * We are adding a new project to the buffer, and initializing the activity buffer
     * with a new ListBuffer 
     */
    currentProjectBuffer += new InputProject(projectName, new ListBuffer[InputActivity])
  }

  def getProject(projectName: String): Option[InputProject] = {
    currentProjectBuffer.foreach(item => {
      if (item.name == projectName) {
        return Some(item)
      }
    })
    return None
  }

  /**
   * Adding activity to the specified project
   */
  def addActivityToProject(projectName: String, activityName: String) = {
    getProject(projectName) match {
      case Some(project) => project.activities += InputActivity(activityName)
      case None          => { /*do nothing*/ }
    }
  }

  /**
   * Saving the projects and activities to file prj.txt
   * and they are sorted.
   */
  def saveProjectsToFile = {
    if (currentProjectBuffer.size > 0) {
      var fileContent = new StringBuilder()
      currentProjectBuffer.sortBy(_.name.toLowerCase()).foreach(project =>{
        fileContent.append("#" + project.name + '\n')
        project.activities.sortBy(_.name.toLowerCase())foreach(activity =>{
          fileContent.append("+" + activity.name+ '\n')
        })
      })
      //println(fileContent.toString())
      FileUtil.saveToFile(Settings.projectFileName, fileContent.toString, false)
    }
  }

  /**
   * finds the last logged item if it is today
   * otherwise it will set the current time
   */
  def getLastLoggedItem = {
    var time = dt.currentTime
    val logLines = FileUtil.readFromFile(Settings.logFileName, Settings.propertiesMap("LogFileEncoding"))
    val itemList = logLines.map(ItemUtil.parseLogItem).filter(_ != null).sortBy(_.end).filter(_.dayValue==dt.getDayValueInMs(dt.currentDay))
    //itemList.map(println)
    var res:Item = null     
    if (itemList != null && itemList.length >0) {
      time = dt.getTimePart(itemList(itemList.length-1).end)
    }
    time
  }
  
  /**
   * this main method is only used for testing.
   */
  def main(args: Array[String]): Unit = {
    //Settings.projectFileName = "/Users/anders/dev/eclipse_ws1/TimeslicerFX/data/prj.txt"
    Settings.loadProperties
    loadProjects
  }
}

