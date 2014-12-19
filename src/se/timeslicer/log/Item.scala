package se.timeslicer.log

import se.timeslicer.util.Util

case class Item(start:String, 
    end:String, 
    project:String, 
    activity:String, 
    comment:String, 
    dayValue:Long) {
	def duration:Long = {
	  Util.elapsedMinutes(start, end)
	}
}