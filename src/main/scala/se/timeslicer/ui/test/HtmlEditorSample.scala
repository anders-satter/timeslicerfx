package se.timeslicer.ui.test


import scalafx.Includes.when
import scalafx.application.JFXApp
import scalafx.beans.property.ReadOnlyBooleanProperty.sfxReadOnlyBooleanProperty2jfx
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.sfxColor2jfx
import scalafx.scene.shape.Rectangle
import scalafx.scene.web.HTMLEditor
import scalafx.scene.layout.Priority
import scalafx.scene.control.Label
import scalafx.scene.layout.VBox
import scalafx.geometry.Insets
import scalafx.scene.control.ScrollPane
import scalafx.scene.control.Button
import scalafx.event.ActionEvent


/*
 * Copyright 2013 ScalaFX Project
 * All right reserved.
 */
 
 
object HtmlEditorSample extends JFXApp {
 
  stage = new JFXApp.PrimaryStage {
    title = "Html Editor Example"
    scene = new Scene {
      root = {
        // Initial Text in the html editor
        val initialText = """<html><body>Lorem ipsum dolor sit amet, consectetur adipiscing elit.
                Nam tortor felis, pulvinar in scelerisque cursus, pulvinar at ante. Nulla consequat
                congue lectus in sodales. Nullam eu est a felis ornare bibendum et nec tellus.
                Vivamus non metus tempus augue auctor ornare. Duis pulvinar justo ac purus adipiscing
                pulvinar. Integer congue faucibus dapibus. Integer id nisl ut elit aliquam sagittis
                gravida eu dolor. Etiam sit amet ipsum sem.</body></html>"""
     
        val htmlEditor = new HTMLEditor {
          prefHeight = 250
          hgrow = Priority.Always
          vgrow = Priority.Always
          htmlText = initialText
        }
     
        val htmlLabel = new Label {
          wrapText = true
        }
     
        new VBox {
          spacing = 5
          padding = Insets(5)
          content = List(
            htmlEditor,
            new Button {
              text = "Display Html below"
              //onAction = (ae: ActionEvent) => htmlLabel.text = htmlEditor.htmlText
            },
            new ScrollPane {
              prefHeight = 200
              hgrow = Priority.Always
              vgrow = Priority.Always
              content = htmlLabel
            }
          )
        }
      }
    }
  }
}