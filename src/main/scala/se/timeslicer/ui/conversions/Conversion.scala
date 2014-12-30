package se.timeslicer.ui.conversions

import scalafx.collections.ObservableBuffer

/**
 * Conversion functions to be used to conversions to be used in the
 * ui
 */
object Conversion {
  def getObservableBuffer(sequence:Seq[String]): ObservableBuffer[String] = {
    val buffer:ObservableBuffer[String] = new ObservableBuffer()
    sequence.map(item => {
      buffer += item
    })
    return buffer
  }
}