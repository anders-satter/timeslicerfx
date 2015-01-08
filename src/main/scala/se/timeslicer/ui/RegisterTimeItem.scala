package se.timeslicer.ui

import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.Includes.handle
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.Includes.observableList2ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.control.TextField
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.HBox
import scalafx.scene.layout.VBox
import scalafx.scene.text.Text
import scalafx.stage.Stage
import se.timeslicer.util.DateTime
import se.timeslicer.input.InputHelper

/**
 * A simple dialog for supplying input text
 */
object RegisterTimeItem {

  private val dateLabel = new Text {
    text = "Date"
  }
  private val dateField = new TextField {
    text = ""
  }
  private val startTimeLabel = new Text {
    text = "Start Time"
  }

  private val startTimeField = new TextField {
    text = ""
  }
  private val endTimeLabel = new Text {
    text = "End Time"
  }

  private val endTimeField = new TextField {
    text = ""
  }

  private val projectLabel = new Text {
    text = "Project"
  }

  private val projectField = new TextField {
    text = ""
  }

  private val activityLabel = new Text {
    text = "Activity"
  }

  private val activityField = new TextField {
    text = ""
  }
  private val commentLabel = new Text {
    text = "Comment"
  }

  private val commentField = new TextField {
    text = ""
  }

  var lastSavedTime: String = "";

  def createItemString: String = {
    val str: StringBuilder = new StringBuilder()
    str.append(dateField.text.value + " " + startTimeField.text.value + '\t')
    str.append(dateField.text.value + " " + endTimeField.text.value + '\t')
    str.append("0" + '\t')
    str.append('"' + projectField.text.value + '"' + '\t')
    str.append('"' + activityField.text.value + '"' + '\t')
    str.append('"' + commentField.text.value + '"')
    str.toString
  }

  def showDialog(projectName: String, activityName: String, formatting: String) {
    dateField.text = DateTime.currentDay
    projectField.text = projectName
    activityField.text = activityName
    if (lastSavedTime == "") {
      //
      /**
       * getLatestLoggedItem
       * if it was made today, get its
       * end time and put it in the end time box
       */
    }
    //startTimeField.text = lastSavedTime
    startTimeField.text = InputHelper.getLastLoggedItem
    endTimeField.text = DateTime.currentTime

    val dialogStage = new Stage {
      outer => {
        width = 200
        height = 300
        title = "Register Time Item"
        scene = new Scene {
          root = new VBox {
            onKeyPressed = { ke: KeyEvent =>
              {
                if (ke.code == KeyCode.ESCAPE) {
                  //resultCallback(false, "")
                  outer.close()
                }
              }
            }
            stylesheets += formatting
            padding = Insets(10)
            content = Seq(
              dateLabel,
              dateField,
              startTimeLabel,
              startTimeField,
              endTimeLabel,
              endTimeField,
              projectLabel,
              projectField,
              activityLabel,
              activityField,
              commentLabel,
              commentField,
              new HBox {
                spacing = 10
                padding = Insets(10)
                content = Seq(
                  new Button {
                    text = "Confirm"
                    onMouseClicked = handle {
                      lastSavedTime = endTimeField.text.value
                      InputHelper.saveLogItemToFile(createItemString)
                      outer.close()
                    }
                    onKeyReleased = { ke: KeyEvent =>
                      {
                        if (ke.code == KeyCode.ENTER) {
                          lastSavedTime = endTimeField.text.value
                          InputHelper.saveLogItemToFile(createItemString)
                          outer.close()
                        }
                      }
                    }
                  },
                  new Button {
                    text = "Cancel"
                    onMouseClicked = handle {
                      outer.close()
                    }
                    onKeyReleased = { ke: KeyEvent =>
                      {
                        if (ke.code == KeyCode.ENTER) {
                          outer.close()
                        }
                      }
                    }
                  })
              })
          }
        }
      }
    }
    // Show dialog and wait till it is closed
    dialogStage.showAndWait()
  }
}