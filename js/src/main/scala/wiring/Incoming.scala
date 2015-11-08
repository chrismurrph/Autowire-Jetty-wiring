package wiring

/**
 * User: Chris
 * Date: 10/08/2015
 * Time: 12:48 AM
 */

import scala.scalajs.js.annotation.JSExport

@JSExport
object Incoming {
  @JSExport
  def logon():Unit = {
    ServerFacing.logon()
  }
  @JSExport
  def test(s1:String, s2:String, o1:Any):Unit = {
    println(s"Inside test with: $s1, $s2, $o1")
    val str = o1.toString
    println(s"toString worked: $str")
  }
  @JSExport
  def requestGraphLine(startTimeStr: String,
                       endTimeStr: String,
                       physicalSubstanceNames: Any,
                       displayName: String,
                       displayTimeStr: String):Unit = {
    println(s"In requestGraphLine abt to call ServerFacing.requestGraphLine with physicalSubstanceNames: $physicalSubstanceNames")
    val parser = CljsVectorParser
    parser.parse(physicalSubstanceNames.toString)
    println(s"Elements found: ${parser.elements}")
    ServerFacing.requestGraphLine(startTimeStr,endTimeStr,parser.elements,displayName,displayTimeStr)
  }
}