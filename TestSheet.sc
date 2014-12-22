import se.timeslicer.log.LogItem
import scala.collection.mutable.ListBuffer
object TestSheet {
	class It(var name:String, var result:Int)
	
	val l = Array(new It("a", 23), new It("b", 18))
                                                  //> l  : Array[TestSheet.It] = Array(TestSheet$It@13fee20c, TestSheet$It@4e04a76
                                                  //| 5)
	
	
	l.map(_.result).reduceLeft(_ + _)         //> res0: Int = 41
	
	def myAdd(x:Int, y:Int) = x + y           //> myAdd: (x: Int, y: Int)Int
	
	//myAdd( 2, 3).apply(myAdd(_, 34))
	
	
	
}