package wiring

object Constants{
  val WebAppDirName = "exampleWiring"
}

case class GraphPoint(min:String, max:String, avg:String, sampleTime:String, minValTime:String, maxValTime: String, periodType: String){
  //println(s"GraphPoint being constructed with min,max,avg: $min, $max, $avg")
}

trait Api{
  def list(path: String): Seq[String]
  def browserLogon(s: String): Option[String]
  def requestGraphLine(startTimeStr: String,
                       endTimeStr: String,
                       physicalSubstanceNames: Seq[String],
                       displayName: String,
                       displayTimeStr: String):Map[String,List[GraphPoint]]
}