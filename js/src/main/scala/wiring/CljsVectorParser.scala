package wiring

import fastparse.all._

/**
 * User: Chris
 * Date: 01/09/2015
 * Time: 10:26 PM
 */
object CljsVectorParser {
  //val input = "[One Two Three]"

  var elements:Vector[String] = Vector()

  //These yet to be ported (from #1802 they are coming)
  def isLetter(ch:Char) = ch.isUpper || ch.isLower
  def isUnderscoreChar(ch:Char) = ch == '_'
  def isDashChar(ch:Char) = ch == '-'
  def isVarLike(ch:Char) = isLetter(ch) || isUnderscoreChar(ch) || isDashChar(ch) || ch.isDigit
  def isSpaceChar(ch:Char) = ch == ' '

  val leftB = P(CharIn("["))
  val rightB = P(CharIn("]"))
  val quote = P(CharIn("\""))
  val spaceChar = P(CharPred( (ch:Char) => {isSpaceChar(ch)}))

  val wholeWord = P(CharPred( (ch:Char) => {isVarLike(ch)}).rep(1).!) map createName
  val quotedWholeWord = quote ~ wholeWord ~ quote
  val spaceBeforeWholeWord = spaceChar ~ quotedWholeWord
  val bracketContents = (quotedWholeWord | spaceBeforeWholeWord).rep
  val outerBrackets = leftB ~ bracketContents ~ rightB
  val target = outerBrackets ~ End
  def parse(str: String) = target.parse(str)

  private def createName(name:String):Unit = {
    //println(s"CREATED name: $name")
    elements = elements :+ name
  }

  /* Turn into (extend) App and uncomment to test
  val res = parse(input)
  println(res)
  println(elements)
  */
}
