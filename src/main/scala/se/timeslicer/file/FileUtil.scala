package se.timeslicer.file
import scala.io.Source
import java.io.FileWriter
import java.io.InputStream
import java.io.FileInputStream
import scala.util.Properties

/**
 * Different file reading routines, to be used
 * by other parts of the program
 */
object FileUtil {
  /**
   * Returning lines from a text file
   */
//  def readFromFile(filename: String): Array[String] = {
//    //println(Properties.encodingString)
//    //Source.fromFile(filename).getLines.toArray
//    toSource(new FileInputStream(filename),Properties.encodingString).getLines.toArray
//  }
  
  def readFromFile(filename: String, encoding: String): Array[String] = {
    println(Properties.encodingString)
    toSource(new FileInputStream(filename),encoding).getLines.toArray
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

  
  def toSource(inputStream: InputStream, decoding: String): scala.io.BufferedSource = {
    import java.nio.charset.Charset
    import java.nio.charset.CodingErrorAction
    val decoder = Charset.forName(decoding).newDecoder()
    decoder.onMalformedInput(CodingErrorAction.IGNORE)
    scala.io.Source.fromInputStream(inputStream)(decoder)
  }
}
