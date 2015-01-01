package se.timeslicer.reporting

import scalafx.scene.control.TextField
import scalafx.scene.control.Label
import scalafx.scene.control.TextArea
import se.timeslicer.ui.ControlFactory
import scalafx.geometry.Pos
import se.timeslicer.util.DateTime
import se.timeslicer.file.FileUtil
import se.timeslicer.util.ItemUtil

/**
 * Reporting page object
 */
object ReportingHelper {
  /*---------------
   * CONTROLS
   * --------------
   */
  private val startDayLabel = new Label { text = "Start Day:" }
  private val startDayField = new TextField { text = "" }
  private val endDayLabel = new Label { text = "End Day:" }
  private val endDayField = new TextField { text = "" }
  private val submitButton = ControlFactory.button("Submit", onSubmitButtonHandler)
  private val summaryTextArea = new TextArea {
    text = "empty"
    prefHeight = 900
  }

  /*----------------
   * VARIABLES
   * ---------------
   */
  private var currentStartDay: String = ""
  private var currentEndDay: String = ""

  /*--------------------
   * INITIALIZATIONS
   * -------------------
   */

  private def init = {
    if (startDayField.text.value == "") {
      startDayField.text = DateTime.currentDay
    }

    if (endDayField.text.value == "") {
      endDayField.text = DateTime.currentDay
    }
  }

  def page = {
    init
    val inputBox = ControlFactory.hbox(Seq(startDayLabel, startDayField, endDayLabel, endDayField, submitButton))
    inputBox.alignment = Pos.Center
    val outBox = ControlFactory.vbox(Seq(inputBox, summaryTextArea))
    outBox.autosize()    
    outBox
  }

  private def setInputDays = {
    currentStartDay = startDayField.text.value
    currentEndDay = endDayField.text.value
  }
  private def onSubmitButtonHandler() = {
    setInputDays
    compileReport
  }

  /**
   * Compiles the textual summary report
   */
  private def compileReport = {
    val logLines = FileUtil.readFromFile("/Users/anders/dev/eclipse_ws1/TimeslicerFX/data/log.txt")

    val interval = new IntervalResult()
    interval.start = currentStartDay
    interval.end = currentEndDay

    val itemList = logLines.map(ItemUtil.parseLogItem).filter(_ != null).sortBy(_.dayValue)

    /*
     * convert the list to items
     */
    interval.itemList = itemList

    interval.selection = ItemUtil.itemsInInterval(interval.itemList, interval.start, interval.end)
    if (interval.selection.length > 0) {

      /* We have log items in the selection
       * map.key = project, value = array of activities 
       */
      val byProject = interval.selection.groupBy(_.project)

      interval.projectList = byProject.map(entry => {
        new Project(entry._1, entry._2)
      })
      interval.projectList.foreach(_.compile)
      interval.totalTime = interval.projectList.map(_.totalTime).reduceLeft(_ + _)
      summaryTextArea.text = interval.present.toString
    } else {
      summaryTextArea.text = "No items found"
    }
  }
}