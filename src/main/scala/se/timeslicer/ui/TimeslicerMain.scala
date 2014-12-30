package se.timeslicer.ui

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.beans.property.ReadOnlyBooleanProperty.sfxReadOnlyBooleanProperty2jfx
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.sfxColor2jfx
import scalafx.scene.shape.Rectangle
import scalafx.scene.layout.VBox
import scalafx.scene.control.CheckBox
import scalafx.scene.control.Label
import scalafx.geometry.Insets
import scalafx.geometry.Pos
import scalafx.scene.text.Text
import scalafx.scene.text.Font
import scalafx.scene.paint.Color._
import scalafx.scene.layout.StackPane
import scalafx.scene.control.SplitPane
import scalafx.scene.control.ListView
import se.timeslicer.input.InputManagerHelper
import se.timeslicer.ui.conversions.Conversion
import scalafx.scene.control.SelectionMode
import javafx.scene.text.Font
import scalafx.scene.Group
import scalafx.scene.layout.HBox
import scalafx.scene.input.MouseEvent
import scalafx.collections.ObservableBuffer
import scalafx.scene.input.KeyEvent
import scalafx.scene.control.Button
import scalafx.event.ActionEvent
import org.controlsfx.dialog.Dialogs
import scalafx.stage.Stage
import scalafx.scene.layout.BorderPane
import se.timeslicer.util.FXUtils
import scalafx.scene.control.TextField
import scalafx.scene.input.KeyCode

/**
 * Main entry for the Timeslicer application
 */
object TimeslicerMain extends JFXApp {
  /*-----------
   * FORMATTING
   * ----------
   */
  val globalTextFormatting = this.getClass.getResource("/application.css").toExternalForm

  /*---------------------
   * INITIALIZATION
   * --------------------
   */
  def reloadProjects = Conversion.getObservableBuffer(InputManagerHelper.currentProjectBuffer.sortBy(_.name.toLowerCase()).map(_.name).toSeq)
  InputManagerHelper.projectFileName = "/Users/anders/dev/eclipse_ws1/TimeslicerFX/data/prj.txt"
  InputManagerHelper.logFileName = "/Users/anders/dev/eclipse_ws1/TimeslicerFX/data/log.txt"
  
  InputManagerHelper.loadProjects

  /*
   * --------
   * CONTROLS
   * --------
   */
  val projectsLabel = new Label {
    text = "Project"
  }

  val prjListView = new ListView[String] {
    maxWidth = 200
    items = reloadProjects
    selectionModel().setSelectionMode(SelectionMode.SINGLE)
    onMouseClicked = { (e: MouseEvent) => { onProjectSelectHandler(selectionModel().getSelectedItems) } }
    onKeyReleased = { (e: KeyEvent) => { onProjectSelectHandler(selectionModel().getSelectedItems) } }
  }

  val addProjectButton = new Button {
    text = "Add project"
    onMouseClicked = { (ae: MouseEvent) => { showAddProjectDialog } }
    onKeyPressed = { (ke: KeyEvent) =>
      {
        if (ke.code == KeyCode.ENTER) {
          showAddProjectDialog
        }
      }
    }
  }

  /*
   * this should show the activity list of the chosen project
   */
  val activitesLabel = new Label {
    text = "Activities"
  }

  val actListView = new ListView[String] {
    maxWidth = 600
    selectionModel().setSelectionMode(SelectionMode.SINGLE)
    onMouseClicked = { (e: MouseEvent) => { onActivitySelectHandler(selectionModel().getSelectedItems) } }
    onKeyReleased = { (e: KeyEvent) => { onActivitySelectHandler(selectionModel().getSelectedItems) } }
  }

  val addActivityButton = new Button {
    text = "Add Activity"
    onMouseClicked = { (ae: MouseEvent) => { showAddActivityDialog } }
    onKeyPressed = { (ke: KeyEvent) =>
      {
        if (ke.code == KeyCode.ENTER) {
          showAddActivityDialog
        }
      }
    }
  }

  val registerTimeItemButton = new Button {
    text = "Register Time Item"
    onMouseClicked = { (ae: MouseEvent) => { showRegisterTimeItemDialog } }
    onKeyPressed = { (ke: KeyEvent) =>
      {
        if (ke.code == KeyCode.ENTER) {
          showRegisterTimeItemDialog
        }
      }
    }

  }
  /*
   * save current project and current activity
   */
  var currentProject = ""
  var currentActivity = ""

  /*---------------
   * HANDLERS
   * -------------
   */
  def onProjectSelectHandler(projectName: ObservableBuffer[String]) = {
    actListView.items = InputManagerHelper.getActivitiesForProject(projectName(0))
    currentProject = projectName(0)
  }

  def onActivitySelectHandler(activityName: ObservableBuffer[String]) = {
    currentActivity = activityName(0)
  }

  def showAddProjectDialog = {
    FXUtils.runAndWait {
      SimpleDialog.showDialog("Add project", "Supply name for a new project", {
        (result, projectName) =>
          {
            if (result == true) {
              InputManagerHelper.addProject(projectName)
              InputManagerHelper.saveProjectsToFile
              prjListView.items = reloadProjects
            }
          }
      }, globalTextFormatting)
    }
  }

  def showAddActivityDialog = {
    if (currentProject.length() > 0) {
      FXUtils.runAndWait {
        SimpleDialog.showDialog("Add activity", "Supply name for a new activity in project " + currentProject, {
          (result, activityName) =>
            {
              if (result == true) {
                InputManagerHelper.addActivityToProject(currentProject, activityName)
                InputManagerHelper.saveProjectsToFile
                actListView.items = InputManagerHelper.getActivitiesForProject(currentProject)
              }
            }
        }, globalTextFormatting)
      }
    }
  }

  def showRegisterTimeItemDialog = {
      FXUtils.runAndWait {
        RegisterTimeItem.showDialog(currentProject, currentActivity, globalTextFormatting)
      }
  }
  
  val projectBox = new VBox {
    padding = Insets(10)
    spacing = 5
    alignment = Pos.TopLeft
    content = List(projectsLabel, prjListView, addProjectButton)
  }

  val activityBox = new VBox {
    padding = Insets(10)
    spacing = 5
    alignment = Pos.TopLeft
    content = List(activitesLabel, actListView, addActivityButton, registerTimeItemButton)
  }

  val listViewsBox = new HBox {
    padding = Insets(20)
    spacing = 10
    content = List(projectBox, activityBox)
  }

  /*
   * all the world is a stage, and each must play a part... 
   */
  stage = new JFXApp.PrimaryStage {
    title.value = "Timeslicer"
    width = 700
    height = 600
    scene = new Scene {
      root = {
        stylesheets += globalTextFormatting
        new StackPane {
          content = new SplitPane {
            padding = Insets(20)
            dividerPositions_=(0.20, 0.80)
            items ++= Seq(listViewsBox)
          }
        }
      }
    }
  }
}

