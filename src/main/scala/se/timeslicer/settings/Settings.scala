package se.timeslicer.settings

import scala.collection.JavaConverters._
import se.timeslicer.file.FileUtil
import java.util.Properties
import scala.util.Properties
import scala.io.Source
import java.io.FileInputStream
import scala.collection.mutable.HashMap
import se.timeslicer.util.DateTime
/*
 * Reads settings from the settings.properties file
 */
object Settings {
  
  private var _logFileName: String = ""
  def logFileName = _logFileName
  def logFileName_=(value: String): Unit = _logFileName = value

  private var _projectFileName: String = ""
  def projectFileName = _projectFileName
  def projectFileName_=(value: String): Unit = _projectFileName = value

  private var _settingsFileName: String = ""
  def settingsFileName = _settingsFileName
  def settingsFileName_=(value: String): Unit = _settingsFileName = value

  
  private val _propertiesMap: HashMap[Any, String] = new HashMap()
  def propertiesMap = _propertiesMap
  
  private val _specialWorkdays: HashMap[String, Double] = new HashMap()
  def specialWorkdays = _specialWorkdays

  def loadProperties = {
    val filename = "/Users/anders/dev/eclipse_ws1/TimeslicerFX/data/settings.properties"
    var properties: Properties = new Properties
    properties.load(new FileInputStream(filename))    
    /*
     * NB the propNames list is only traversable ONCE!
     * 
     * println(propNames) results in: non-empty iterator
     * after foreach:
     * println(propNames) results in: empty iterator
     * 
     */
    val propNames = properties.propertyNames().asScala
    propNames.foreach(item => {
      _propertiesMap += (item -> properties.getProperty(item.toString()))
      if (DateTime.isDay(item.toString)) {
        println(item)
        _specialWorkdays += (item.toString() -> (properties.getProperty(item.toString)).toDouble)
      }      
    })  
  }

  def main(args: Array[String]): Unit = {
    loadProperties
    println(propertiesMap)
    println(specialWorkdays)
  }
}

/*
 * val x = new Properties
//load from .properties file here.
import scala.collection.JavaConverters._

scala> x.asScala
res4: scala.collection.mutable.Map[String,String] = Map()
 */
