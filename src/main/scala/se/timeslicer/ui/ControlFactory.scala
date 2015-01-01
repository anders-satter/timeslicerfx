package se.timeslicer.ui

import scala.collection.TraversableOnce

import scalafx.Includes.eventClosureWrapperWithParam
import scalafx.Includes.jfxKeyEvent2sfx
import scalafx.Includes.jfxMouseEvent2sfx
import scalafx.Includes.observableList2ObservableBuffer
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.geometry.Pos
import scalafx.scene.Node
import scalafx.scene.control.Button
import scalafx.scene.control.ListView
import scalafx.scene.control.SelectionMode
import scalafx.scene.control.SelectionMode.sfxEnum2jfx
import scalafx.scene.control.SplitPane
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.HBox
import scalafx.scene.layout.VBox
/**
 * Factory for standard controls
 */
object ControlFactory {

  def projectListView(itemBuffer: ObservableBuffer[String], onProjectSelectHandler: (ObservableBuffer[String]) => Unit): ListView[String] = {
    new ListView[String] {
      maxWidth = 200
      items = itemBuffer
      selectionModel().setSelectionMode(SelectionMode.SINGLE)
      onMouseClicked = { (e: MouseEvent) => { onProjectSelectHandler(selectionModel().getSelectedItems) } }
      onKeyReleased = { (e: KeyEvent) => { onProjectSelectHandler(selectionModel().getSelectedItems) } }
    }
  }

  def activityListView(onActivitySelectHandler: (ObservableBuffer[String]) => Unit): ListView[String] = {
    new ListView[String] {
      maxWidth = 600
      selectionModel().setSelectionMode(SelectionMode.SINGLE)
      onMouseClicked = { (e: MouseEvent) => { onActivitySelectHandler(selectionModel().getSelectedItems) } }
      onKeyReleased = { (e: KeyEvent) => { onActivitySelectHandler(selectionModel().getSelectedItems) } }
    }
  }

  def button(buttonText: String, buttonOnActionHandler: () => Unit): Button = {
    new Button {
      text = buttonText
      onMouseClicked = { (ae: MouseEvent) => { buttonOnActionHandler() } }
      onKeyPressed = { (ke: KeyEvent) =>
        {
          if (ke.code == KeyCode.ENTER) {
            buttonOnActionHandler()
          }
        }
      }
    }
  }

  def vbox(contentSeq: Iterable[Node]): VBox = {
    new VBox {
      padding = Insets(10)
      spacing = 5
      alignment = Pos.TopLeft
      content = contentSeq
    }
  }
  def hbox(contentSeq: Iterable[Node]): HBox = {
    new HBox {
      padding = Insets(10)
      spacing = 5
      alignment = Pos.TopLeft
      content = contentSeq
    }
  }

  def stackPane(contentNodeIterable: TraversableOnce[javafx.scene.Node]):SplitPane = {
    new SplitPane {
      padding = Insets(5)
      dividerPositions_=(0.20, 0.80)
      items ++= contentNodeIterable
    }  
  }
  
}
