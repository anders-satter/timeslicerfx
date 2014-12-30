package se.timeslicer.file
import scala.io.Source
import java.io.FileWriter

/**
 * Different file reading routines, to be used
 * by other parts of the program
 */
object FileUtil {
  /**
   * Returning lines from a text file
   */
  def readFromFile(filename: String): Array[String] = {
    Source.fromFile(filename).getLines.toArray
  }

  /**
   * Saving a text file, if append=true the text will be appended
   * to the file, otherwise the content will overwrite everything in the file
   */
  def saveToFile(filename: String, content: String, append: Boolean) = {
    val fw = new FileWriter(filename, append)
    fw.write(content)
    fw.close
  }
}
