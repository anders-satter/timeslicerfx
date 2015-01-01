package se.timeslicer.ui.test
/*
 * Copyright 2013 ScalaFX Project
 * All right reserved.
 */

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control.SplitPane
import scalafx.scene.layout.{ StackPane, Region }
import scalafx.scene.text.Text
import scalafx.scene.layout.VBox
import scalafx.geometry.Pos
import scalafx.scene.control.SelectionMode
import scalafx.scene.control.ListView
import scalafx.collections.ObservableBuffer
import se.timeslicer.input.InputHelper
import se.timeslicer.ui.conversions.Conversion
import se.timeslicer.settings.Settings

/**
 * A sample that demonstrates styling a hidden split pane with CSS.
 *
 * @see javafx.scene.control.SplitPane
 * @resource /scalafx/ensemble/css/HiddenSplitPane.css
 */
object HiddenSplitPaneSample extends JFXApp {

  // Region that will be used in the split pane
  var text = new Text {
    text = "Hello"
  }

  val listView = new ListView[String] {
    maxWidth = 200
    Settings.projectFileName = "/Users/anders/dev/eclipse_ws1/TimeslicerFX/data/prj.txt"
    items = Conversion.getObservableBuffer(InputHelper.loadProjects.map(_.name).toSeq)
    selectionModel().setSelectionMode(SelectionMode.SINGLE)
  }

  val b = new VBox {
    padding = Insets(20)
    spacing = 10
    alignment = Pos.TopLeft
    content = List(text, listView)
  }

  stage = new JFXApp.PrimaryStage {
    title = "Hidden Split Pane Example"
    width = 600
    height = 400
    scene = new Scene {
      root = {

        //Style Sheet loaded from external 
        //val hiddenSplitPaneCss = this.getClass.getResource("/scalafx/ensemble/css/HiddenSplitPane.css").toExternalForm

        val reg1 = new Region {
          //styleClass = List("rounded")
          content = List(b)
        }
        val reg2 = new Region {
          styleClass = List("rounded")
        }
        val reg3 = new Region {
          styleClass = List("rounded")
        }

        new StackPane {
          content = new SplitPane {
            padding = Insets(20)
            dividerPositions_=(0.20, 0.80)
            items ++= Seq(b)

            id = "hiddenSplitter"
            // stylesheets += hiddenSplitPaneCss
          }
        }
      }
    }
  }
}