package se.timeslicer.reporting

import scalafx.scene.control.TextField
import scalafx.scene.control.Label
import scalafx.scene.control.TextArea
import se.timeslicer.ui.ControlFactory
import scalafx.geometry.Pos
import se.timeslicer.util.DateTime
import se.timeslicer.file.FileUtil
import se.timeslicer.util.ItemUtil
import scalafx.scene.control.TableColumn
import scalafx.scene.control.TableView
import scalafx.scene.layout.VBox
import scalafx.scene.control.SplitPane
import scalafx.geometry.Orientation
import se.timeslicer.settings.Settings
import scalafx.beans.property.StringProperty
import scalafx.scene.layout.Background
import scalafx.scene.control.TableCell
import javafx.beans.value.ObservableStringValue
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

/**
 * Reporting page object
 */
object ReportingHelper {
  /*---------------
   * CONTROLS
   * --------------
   */
  private val startDayLabel = new Label { text = "Start Day:" }
  private val startDayField = new TextField { text = "2013-11-01" }
  private val endDayLabel = new Label { text = "End Day:" }
  private val endDayField = new TextField { text = "2013-12-31" }
  private val submitButton = ControlFactory.button("Submit", generateReport)
  private val summaryTextArea = new TextArea {
    text = ""
    prefHeight = 600
  }
  /**
   * placeholder of an empty Vbox, to have something to show
   * when the table is empty
   * This is instantiated in createCalendarTableView
   */
  private val tableViewBox: VBox = new VBox {}

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

  /**
   * Returns the page to be used by the main application
   * This is not the best way to do it all, as there are no others to l
   */
  def page = {
    init
    val inputBox = ControlFactory.hbox(Seq(startDayLabel, startDayField, endDayLabel, endDayField, submitButton))
    inputBox.alignment = Pos.BottomCenter
    inputBox.prefHeight = 10
    inputBox.spacing = 5

    val splitPane = new SplitPane {
      orientation = Orientation.VERTICAL
      items.addAll(summaryTextArea, tableViewBox)
    }

    val outBox = ControlFactory.vbox(Seq(inputBox, splitPane))
    outBox
  }

  /**
   * Generates the report, called for the submit button
   */
  private def generateReport() = {
    setInputDays
    val intervalResult = getIntervalResult
    if (intervalResult.selection.length > 0) {
      createTotalSummaryReport(intervalResult)
      createDaySummaryReport(intervalResult)
    } else {
      summaryTextArea.text = "No items found"
    }
  }

  private def setInputDays = {
    currentStartDay = startDayField.text.value
    currentEndDay = endDayField.text.value
  }

  private def getIntervalResult: IntervalResult = {
    val logLines = FileUtil.readFromFile("/Users/anders/dev/eclipse_ws1/TimeslicerFX/data/log.txt")

    val interval = new IntervalResult()
    interval.start = currentStartDay
    interval.end = currentEndDay

    val itemList = logLines.map(ItemUtil.parseLogItem).filter(_ != null).sortBy(_.dayValue)

    /*
     * convert the list to items
     */
    interval.itemList = itemList.filter(item => Settings.isCalculable(item.activity))
    interval.notCalculatedItemList = itemList.filter(item => Settings.isCalculable(item.activity) == false)

    interval.selection = ItemUtil.itemsInInterval(interval.itemList, interval.start, interval.end)
    return interval

  }

  /**
   * Compiles the textual summary report
   */
  private def createTotalSummaryReport(interval: IntervalResult) = {

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

  }

  private class DayType() {
    private val dt = DateTime
    private var _currentDay: Long = 0
    def currentDay:Long = _currentDay   
    def currentDay_=(value: Long):Unit = {_currentDay = value}
    def currentDay_=(value: String):Unit = {_currentDay = dt.getDayValueInMs(value)}
    def isSaturday = {dt.isSaturday(_currentDay)}
    def isSunday = {dt.isSunday(_currentDay)}
  }
  
  private def createDaySummaryReport(interval: IntervalResult) = {
    val daySumMap = interval.daySumMap
    val dayResultTableRowBuffer = DayResultHelper.dayResultTableRowBuffer(DateTime.getDayValueInMs(interval.start), DateTime.getDayValueInMs(interval.end), daySumMap)

    val calendarTableView = new TableView[DayResultTableRow](dayResultTableRowBuffer) {
      editable = false
    }

    /*
     * Holiday closures
     */

    /*
     * Table names
     */
    val dayCol = new TableColumn[DayResultTableRow, String] {
      text = "Day"
      var isRed = false
      cellValueFactory = { cellData =>
        {
          val dn = cellData.value.dayName.value
          if (dn.trim().toLowerCase().startsWith("sun") ||
            dn.trim().toLowerCase().startsWith("sat")) {
            isRed = true
          } else {
            isRed = false
          }
          cellData.value.day
        }
      }

      cellFactory = { i =>
        //println(i.getCellValueFactory().)
        val cell = new TableCell[DayResultTableRow, String]
        cell.itemProperty().addListener(new ChangeListener[String] {
          override def changed(obs: ObservableValue[_ <: String], oldItem: String, newItem: String): Unit = {
            cell.setText(newItem)
            if (isRed) {
              cell.getStyleClass().add("not-workday")
            } else {
              cell.getStyleClass().remove("not-workday")
            }
          }
        })
        cell
      }
    }
    val dayNameCol = new TableColumn[DayResultTableRow, String] {
      text = "Day Name"
      cellValueFactory = { _.value.dayName }
    }
    val durationCol = new TableColumn[DayResultTableRow, String] {
      text = "Hours"
      cellValueFactory = { _.value.hours }
    }
    val normalTime = new TableColumn[DayResultTableRow, String] {
      text = "NT"
      cellValueFactory = { _.value.normalTime }
    }

    val wtMinusNT = new TableColumn[DayResultTableRow, String] {
      text = "WT-NT"
      cellValueFactory = { _.value.diffWtNt }
    }

    val accWt = new TableColumn[DayResultTableRow, String] {
      text = "AccWT"
      cellValueFactory = { _.value.accWt }
    }

    val accNt = new TableColumn[DayResultTableRow, String] {
      text = "AccNT"
      cellValueFactory = { _.value.accNt }
    }

    val diffAccWtNt = new TableColumn[DayResultTableRow, String] {
      text = "AccWT-AccNT"
      cellValueFactory = { _.value.diffAccWtNt }
    }

    calendarTableView.columns ++= List(dayCol, dayNameCol, durationCol, normalTime, wtMinusNT, accWt, accNt, diffAccWtNt)
    tableViewBox.content = calendarTableView
    tableViewBox.requestLayout()
  }

  def main(args: Array[String]): Unit = {
    val d = dayType()
    d()
    ("setDayStr")("2015-01-03")
    

}