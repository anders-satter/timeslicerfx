package se.timeslicer.ui

import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.Includes.jfxMouseEvent2sfx
import scalafx.Includes.observableList2ObservableBuffer
import scalafx.application.JFXApp
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.control.Label
import scalafx.scene.control.TextArea
import scalafx.scene.control.TextArea.sfxTextArea2jfx
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox.sfxHBox2jfx
import scalafx.scene.layout.StackPane
import scalafx.scene.layout.VBox
import se.timeslicer.input.InputHelper
import se.timeslicer.ui.conversions.Conversion
import se.timeslicer.util.FXUtils
import se.timeslicer.reporting.ReportingHelper

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
  var currentProject = ""
  var currentActivity = ""
  var isRegisterPage = true

  InputHelper.projectFileName = "/Users/anders/dev/eclipse_ws1/TimeslicerFX/data/prj.txt"
  InputHelper.logFileName = "/Users/anders/dev/eclipse_ws1/TimeslicerFX/data/log.txt"
  InputHelper.loadProjects

  /*
   * --------
   * CONTROLS
   * --------
   */
  /* Project management*/
  val projectsLabel = new Label { text = "Project" }
  val prjListView = ControlFactory.projectListView(reloadProjects, onProjectSelectHandler)
  val addProjectButton = ControlFactory.button("Add Project", showAddProjectDialog)

  /* Activity management*/
  val activitesLabel = new Label { text = "Activities" }
  val actListView = ControlFactory.activityListView(onActivitySelectHandler)
  val addActivityButton = ControlFactory.button("Add Activity", showAddActivityDialog)

  /* Register time item */
  val registerTimeItemButton = ControlFactory.button("Register Time Item", showRegisterTimeItemDialog)

  /*---------------
   * HANDLERS
   * -------------
   */
  def reloadProjects = Conversion.getObservableBuffer(InputHelper.currentProjectBuffer.sortBy(_.name.toLowerCase()).map(_.name).toSeq)
  def onProjectSelectHandler(projectName: ObservableBuffer[String]) = {
    actListView.items = InputHelper.getActivitiesForProject(projectName(0))
    currentProject = projectName(0)
  }
  def onActivitySelectHandler(activityName: ObservableBuffer[String]) = {
    currentActivity = activityName(0)
  }

  def showAddProjectDialog() = {
    FXUtils.runAndWait {
      SimpleDialog.showDialog("Add project", "Supply name for a new project", {
        (result, projectName) =>
          {
            if (result == true) {
              InputHelper.addProject(projectName)
              InputHelper.saveProjectsToFile
              prjListView.items = reloadProjects
            }
          }
      }, globalTextFormatting)
    }
  }

  def showAddActivityDialog() = {
    if (currentProject.length() > 0) {
      FXUtils.runAndWait {
        SimpleDialog.showDialog("Add activity", "Supply name for a new activity in project " + currentProject, {
          (result, activityName) =>
            {
              if (result == true) {
                InputHelper.addActivityToProject(currentProject, activityName)
                InputHelper.saveProjectsToFile
                actListView.items = InputHelper.getActivitiesForProject(currentProject)
              }
            }
        }, globalTextFormatting)
      }
    }
  }

  def showRegisterTimeItemDialog() = {
    FXUtils.runAndWait {
      RegisterTimeItem.showDialog(currentProject, currentActivity, globalTextFormatting)
    }
  }

  
  /*---------
   *  LAYOUT
   * --------
   */  
  val projectBox = ControlFactory.vbox(Seq(projectsLabel, prjListView, addProjectButton))
  val activityBox = ControlFactory.vbox(Seq(activitesLabel, actListView, addActivityButton, registerTimeItemButton))  
  /* put both boxes in one hbox to group the together*/
  val listViewsBox = ControlFactory.hbox(Seq(projectBox, activityBox)) 
  
  val reportingTextArea = new TextArea {
    text = "this is where the report is going to be found"
  }

  /*
   * Creation of the button to be used to choose between
   * item registration or reporting 
   */
  val pageToggleButton = new Button {
    text = "To Reporting Page"
    onMouseClicked = { (ae: MouseEvent) =>
      {
        managePage({ (str: String) =>
          {
            text = str
          }
        })
      }
    }
    onKeyPressed = { (ke: KeyEvent) =>
      {
        if (ke.code == KeyCode.ENTER) {
          managePage({ (str: String) =>
            {
              text = str
            }
          })
        }
      }
    }
  }

  /**
   * Sets the content to either the register or
   * reporting page
   */
  def managePage(setTextCallBack: (String) => Unit) = {
    if (isRegisterPage == true) {
      isRegisterPage = false
      setTextCallBack("To Register Page")
      contentBox.content = reportingPage
      contentBox.requestLayout()
    } else {
      isRegisterPage = true
      setTextCallBack("To Reporting Page")
      contentBox.content = registerItemPage
      contentBox.requestLayout()
    }
  }
  
  val toolbarBox = ControlFactory.hbox(List(pageToggleButton))
  val registerItemPage = new StackPane{ content = ControlFactory.stackPane(Seq(listViewsBox))}  
  val reportingPage = new StackPane{content = ControlFactory.stackPane(Seq(ReportingHelper.page))}
  
  /*
   * init the content box with the register page 
   */
  val contentBox = new VBox {
    content = registerItemPage
    autosize()
    
  }

  stage = new JFXApp.PrimaryStage {
    title.value = "Timeslicer"
    width = 600
    height = 550
    scene = new Scene {
      root = {
        stylesheets += globalTextFormatting
        new VBox {
          content = Seq(toolbarBox, contentBox)
        }
      }
    }
  }
}

