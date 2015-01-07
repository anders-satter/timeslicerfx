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
import scalafx.Includes._

/**
 * Reporting page object
 */
object ReportingHelper {
  /*---------------------
   * DEFINITIONS
   * --------------------
   */
  val dt = DateTime

  /*---------------
   * CONTROLS
   * --------------
   */
  private val startDayLabel = new Label { text = "Start Day:" }
  private val startDayField = new TextField { text = "2013-11-01" }
  private val endDayLabel = new Label { text = "End Day:" }
  private val endDayField = new TextField { text = "" }
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
  private val tableViewBox: VBox = new VBox {
    prefHeight = 1000
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
    startDayField.text = dt.currentDay
    if (startDayField.text.value == "") {
    }

    if (endDayField.text.value == "") {
      endDayField.text = dt.currentDay
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
    val logLines = FileUtil.readFromFile(Settings.logFileName, Settings.propertiesMap("LogFileEncoding"))

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

  private def createDaySummaryReport(interval: IntervalResult) = {
    val dayType = new dt.DayType
    val daySumMap = interval.daySumMap
    val dayResultTableRowBuffer = DayResultHelper.dayResultTableRowBuffer(dt.getDayValueInMs(interval.start), dt.getDayValueInMs(interval.end), daySumMap)

    val calendarTableView = new TableView[DayResultTableRow](dayResultTableRowBuffer) {
      editable = false
      prefHeight = 1000
    }
    /**
     * handles the style
     */
    def handleItemAndStyle(item: javafx.scene.control.TableCell[DayResultTableRow, String], text: String) = {
      item.setText(text)
      item.getStyleClass().remove("holiday")
      item.getStyleClass().remove("saturday")

      if (dayType.isSunday) {
        item.getStyleClass().add("holiday")
      }
      if (dayType.isSaturday) {
        item.getStyleClass().add("saturday")
      }
    }

    /*
     * Table names
     */
    val dayCol = new TableColumn[DayResultTableRow, String] {
      text = "Day"
      cellValueFactory = { cellData =>
        {
          dayType.currentDay = cellData.value.day.value
          cellData.value.day
        }
      }
      cellFactory = { column =>
        {
          new javafx.scene.control.TableCell[DayResultTableRow, String] {
            override def updateItem(item: String, empty: Boolean) = {
              handleItemAndStyle(this, item)
            }
            setAlignment(Pos.CENTER)
          }
        }
      }
    }

    val dayNameCol = new TableColumn[DayResultTableRow, String] {
      text = "Day Name"
      prefWidth = 70
      cellValueFactory = { cellData =>
        {
          dayType.currentDay = cellData.value.day.value
          cellData.value.dayName
        }
      }
      cellFactory = { column =>
        {
          new javafx.scene.control.TableCell[DayResultTableRow, String] {
            override def updateItem(item: String, empty: Boolean) = {
              handleItemAndStyle(this, item)
            }
            setAlignment(Pos.CENTER)
          }
        }
      }
    }
    val durationCol = new TableColumn[DayResultTableRow, String] {
      text = "Hours"
      prefWidth = 50
      cellValueFactory = { cellData =>
        {
          dayType.currentDay = cellData.value.day.value
          cellData.value.hours
        }
      }

      cellFactory = { column =>
        {
          new javafx.scene.control.TableCell[DayResultTableRow, String] {
            override def updateItem(item: String, empty: Boolean) = {
              handleItemAndStyle(this, item)
            }
            setAlignment(Pos.CENTER_RIGHT)
          }
        }
      }
    }
    val normalTime = new TableColumn[DayResultTableRow, String] {
      text = "NT"
      prefWidth = 50
      //cellValueFactory = { _.value.hours }
      cellValueFactory = { cellData =>
        {
          dayType.currentDay = cellData.value.day.value
          cellData.value.normalTime
        }
      }

      cellFactory = { column =>
        {
          new javafx.scene.control.TableCell[DayResultTableRow, String] {
            override def updateItem(item: String, empty: Boolean) = {
              handleItemAndStyle(this, item)
            }
            setAlignment(Pos.CENTER_RIGHT)
          }
        }
      }
    }

    val wtMinusNT = new TableColumn[DayResultTableRow, String] {
      text = "WT-NT"
      prefWidth = 65
      cellValueFactory = { cellData =>
        {
          dayType.currentDay = cellData.value.day.value
          cellData.value.diffWtNt
        }
      }

      cellFactory = { column =>
        {
          new javafx.scene.control.TableCell[DayResultTableRow, String] {
            override def updateItem(item: String, empty: Boolean) = {
              handleItemAndStyle(this, item)
            }
            setAlignment(Pos.CENTER_RIGHT)
          }
        }
      }
    }

    val accWt = new TableColumn[DayResultTableRow, String] {
      text = "AccWT"
      prefWidth = 65
      cellValueFactory = { cellData =>
        {
          dayType.currentDay = cellData.value.day.value
          cellData.value.accWt
        }
      }

      cellFactory = { column =>
        {
          new javafx.scene.control.TableCell[DayResultTableRow, String] {
            override def updateItem(item: String, empty: Boolean) = {
              handleItemAndStyle(this, item)
            }
            setAlignment(Pos.CENTER_RIGHT)
          }
        }
      }
    }

    val accNt = new TableColumn[DayResultTableRow, String] {
      text = "AccNT"
      prefWidth = 65
      cellValueFactory = { cellData =>
        {
          dayType.currentDay = cellData.value.day.value
          cellData.value.accNt
        }
      }

      cellFactory = { column =>
        {
          new javafx.scene.control.TableCell[DayResultTableRow, String] {
            override def updateItem(item: String, empty: Boolean) = {
              handleItemAndStyle(this, item)
            }
            setAlignment(Pos.CENTER_RIGHT)
          }
        }
      }
    }

    val diffAccWtNt = new TableColumn[DayResultTableRow, String] {
      text = "AccWT-AccNT"
      prefWidth = 100

      cellValueFactory = { cellData =>
        {
          dayType.currentDay = cellData.value.day.value
          cellData.value.diffAccWtNt
        }
      }

      cellFactory = { column =>
        {
          new javafx.scene.control.TableCell[DayResultTableRow, String] {
            override def updateItem(item: String, empty: Boolean) = {
              handleItemAndStyle(this, item)
            }
            setAlignment(Pos.CENTER_RIGHT)
          }
        }
      }
    }

    calendarTableView.columns ++= List(dayCol, dayNameCol, durationCol, normalTime, wtMinusNT, accWt, accNt, diffAccWtNt)
    tableViewBox.content = calendarTableView
    tableViewBox.prefHeight = 1000
    tableViewBox.requestLayout()
  }

  /**
   * the main method cannot be run here since java fx is not
   * initialized, get Toolkit not initialized
   */
  def main(args: Array[String]): Unit = {
  }

}