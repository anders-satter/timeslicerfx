package se.timeslicer.ui.test

//import scalafx.application.JFXApp
//import scalafx.scene.shape.Circle
//import scalafx.scene.Scene
//import javafx.scene.paint.Color._
//import scalafx.scene.effect.BoxBlur
//import scala.math.random
//import scalafx.Includes._
//import scalafx.animation.Timeline
//import javafx.animation.Animation._
import scalafx.animation.Timeline
import javafx.animation.Animation._
import javafx.scene.effect._
import javafx.scene.paint.Color._
import scala.math.random
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene._
import scalafx.scene.shape._
import scalafx.stage.Stage
import scalafx.scene.input.MouseEvent

object VanishingCircles extends JFXApp {
  var circles: Seq[Circle] = null
  stage = new JFXApp.PrimaryStage {
    title = "Vanishing Circles"
    width = 800
    height = 600
    scene = new Scene {
      fill = BLACK
      circles = for (i <- 0 until 20) yield new Circle {
        centerX = random * 800
        centerY = random * 600
        radius = 150
        fill = color(Math.random, Math.random, Math.random, .2)
        effect = new BoxBlur(10, 20, 3)
        strokeWidth <== when(hover) then 4 otherwise 0
        stroke = WHITE
        /*
         * event listener
         */
        //        onMouseClicked = {
        //          Timeline(at(3 s) {[ radius -> 0 ]}).play()
        //        }

        //        onMouseClicked = { (e: MouseEvent) => 
        //          Timeline(at (3 s) {radius -> 0}).play()          
        //        }

        onMouseClicked = { (e: MouseEvent) => {
            println(e.sceneX)
            Timeline(at(2 ms) { radius -> 0 }).play()
          }
        }
//        onMouseMoved  = { (e: MouseEvent) => {
//            println(e.sceneX)
//            Timeline(at(2 ms) { radius -> 0 }).play()
//          }
//        }
      }
      content = circles
    }
  }
  /*
   * this is for the animation
   */
  new Timeline {
    cycleCount = INDEFINITE
    autoReverse = true
    keyFrames = for (circle <- circles) yield at(30 s) {
      Set(
        circle.centerX -> random * 10,
        circle.centerY -> random * 15)
    }
  }.play();

}