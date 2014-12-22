import se.timeslicer.log.LogItem
import scala.collection.mutable.ListBuffer
object TestSheet {
	class It(var name:String, var result:Int);import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(188); 
	
	val l = Array(new It("a", 23), new It("b", 18));System.out.println("""l  : Array[TestSheet.It] = """ + $show(l ));$skip(39); val res$0 = 
	
	
	l.map(_.result).reduceLeft(_ + _);System.out.println("""res0: Int = """ + $show(res$0));$skip(35); 
	
	def myAdd(x:Int, y:Int) = x + y;System.out.println("""myAdd: (x: Int, y: Int)Int""")}
	
	//myAdd( 2, 3).apply(myAdd(_, 34))
	
	
	
}
