package se.timeslicer.main

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

/**
 * Main entry for the Timeslicer application
 */
object TimeslicerMain extends JFXApp{
  stage = new JFXApp.PrimaryStage {
    title.value = "Timeslicer"
    width = 600
    height = 450
    scene = new Scene {
      fill = Color.AZURE 
      content = new Rectangle {
        x = 25
        y = 40
        width = 100
        height = 100
        //fill <== when (hover) choose Color.Green otherwise Color.Red
        fill <== when (hover) choose Color.White otherwise Color.BLACK 
      }
    }
  }
}

