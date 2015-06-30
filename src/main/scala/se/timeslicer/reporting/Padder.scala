package se.timeslicer.reporting

object Padder {
  def padd(text:String, size:Int):String = {
    val buff = new StringBuilder(text)
    buff.append(" ")
    for(i <- buff.length to size){
        buff.append(" ");      
    }
    buff.toString()
  }
}