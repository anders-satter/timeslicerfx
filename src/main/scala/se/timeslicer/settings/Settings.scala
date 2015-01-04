package se.timeslicer.settings

import scala.collection.JavaConverters._
import se.timeslicer.file.FileUtil
import java.util.Properties
import scala.util.Properties
import scala.io.Source
import java.io.FileInputStream
import scala.collection.mutable.HashMap
import se.timeslicer.util.DateTime
import se.timeslicer.util.ItemUtil
/*
 * Reads settings from the settings.properties file
 */
object Settings {
  
  private var _logFileName: String = ""
  def logFileName = _logFileName
  private def logFileName_=(value: String): Unit = _logFileName = value

  private var _projectFileName: String = ""
  def projectFileName = _projectFileName
  private def projectFileName_=(value: String): Unit = _projectFileName = value

  private var _settingsFileName: String = ""
  def settingsFileName = _settingsFileName
  private def settingsFileName_=(value: String): Unit = _settingsFileName = value
  
  private val _propertiesMap: HashMap[Any, String] = new HashMap()
  def propertiesMap = _propertiesMap
  
  private val _specialWorkdays: HashMap[String, Double] = new HashMap()
  def specialWorkdays = _specialWorkdays

  def loadProperties = {
    settingsFileName = "settings.properties"
    var properties: Properties = new Properties
    properties.load(new FileInputStream(settingsFileName))    
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
        _specialWorkdays += (item.toString() -> (properties.getProperty(item.toString)).toDouble)
      }      
    }) 
    logFileName = propertiesMap("LogFileName")
    projectFileName = propertiesMap("ProjectFileName")
  }
  
  /**
   * Checks against the list NO_CALCULATION_ACTIVITIES in settings.properties  
   */
  def isCalculable(activity: String):Boolean = {
    val excludeActivities = propertiesMap("NO_CALCULATION_ACTIVITIES").split(",").map(_.trim()).map(ItemUtil.removeCitationMarks(_)).toSet
    !excludeActivities.contains(activity)
  }
  def main(args: Array[String]): Unit = {
    loadProperties
    println(propertiesMap)
    println(specialWorkdays)
    println(isCalculable("Lunch"))
    println(logFileName)
    println(projectFileName)
  }
}
