package se.timeslicer.ui

import scalafx.stage.Stage
import scalafx.scene.Scene
import scalafx.scene.text.Text
import scalafx.scene.layout.VBox
import scalafx.geometry.Insets
import scalafx.scene.control.TextField
import scalafx.scene.layout.HBox
import scalafx.scene.control.Button
import scalafx.scene.input.MouseEvent
import scalafx.Includes._
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.KeyCode

/**
 * A simple dialog for supplying input text
 */
object SimpleDialog {

  private val inputField = new TextField {
    text = "<add input here>"
  }

  private val messageText = new Text {
    text = ""
  }

  def showDialog(inTitle: String, message: String, resultCallback: (Boolean, String) => Unit, formatting: String) {
    messageText.text = message
    val dialogStage = new Stage {
      outer => {
        width = 400
        height = 150
        title = inTitle
        scene = new Scene {
          root = new VBox {
            onKeyPressed = { ke: KeyEvent =>
              {
                if (ke.code == KeyCode.ESCAPE) {
                  resultCallback(false, "")
                  outer.close()
                }
              }
            }
            stylesheets += formatting
            padding = Insets(10)
            content = Seq(
              messageText,
              inputField,
              new HBox {
                //def handleConfirm = 
                spacing = 10
                padding = Insets(10)
                content = Seq(
                  new Button {
                    text = "Confirm"
                    onMouseClicked = handle {
                      resultCallback(true, inputField.text.value)
                      outer.close()
                    }
                    onKeyReleased = { ke: KeyEvent =>
                      {
                        if (ke.code == KeyCode.ENTER) {
                          resultCallback(true, inputField.text.value)
                          outer.close()
                        }
                      }
                    }
                  },
                  new Button {
                    text = "Cancel"
                    onMouseClicked = handle {
                      resultCallback(false, "")
                      outer.close()
                    }
                    onKeyReleased = { ke: KeyEvent =>
                      {
                        if (ke.code == KeyCode.ENTER) {
                          resultCallback(false, "")
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