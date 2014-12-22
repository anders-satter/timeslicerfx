package se.timeslicer.file
import scala.io.Source

/**
 * Different file reading routines, to be used
 * by other parts of the program
 */
object FileUtil {

  def readFromFile(filename: String) = {
    Source.fromFile(filename).getLines.toArray
   }
}
