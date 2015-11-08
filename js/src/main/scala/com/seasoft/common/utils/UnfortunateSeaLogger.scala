package com.seasoft.common.utils

import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.joda.time.{DateTime, DateTimeZone}

/**
 * Named `Unfortunate` because: 'SCJS Java 1.7 does not have TimeZone #5'. We will just have to make
 * the `getClientFastBy` call in cljs instead, using `js.Dynamic.global.whatever`
 *
 * If people in different countries are going to be using then they should still see Sydney time
 * where the 'mine' is located, for all trending etc. The user's mind should be concentrated on
 * where the mine is.
 *
 * These improvements will need to be made for the application as well.
 *
 * User: Chris
 * Date: 27/08/2015
 * Time: 9:18 AM
 */
object UnfortunateSeaLogger {
  val MILLISECONDS_DATE_TIME_PARSE_STRING = "dd_MM_yyyy__HH_mm_ss.SSS"
  val MILLISECONDS_DATEFORMAT:DateTimeFormatter = DateTimeFormat.forPattern(MILLISECONDS_DATE_TIME_PARSE_STRING)
  private val TZ: DateTimeZone = DateTimeZone.forID("Australia/Sydney")
  //DateTimeZone.setDefault(TZ)
  //Not yet implemented by Sebastien
  //private val TZ = TimeZone.getTimeZone("Australia/Sydney")
  //MILLISECONDS_DATEFORMAT.setTimeZone(TZ)

  def isBlank(str:String): Boolean = {
    str == null || "" == str
  }

  private def nowOnServersTZ: DateTime = {
    //val calendar: Calendar = new GregorianCalendar()
    //Not yet implemented by Sebastien
    //calendar.setTimeZone(SeaLogger.TZ)
    //val clientTime:Date = calendar.getTime
    //clientTime
    (new DateTime).withZone(TZ)
  }

  def getClientFastBy(serverTimeStr: String): Long = {
    val serverTime:DateTime = parse( serverTimeStr)
    val clientTime:DateTime = nowOnServersTZ
    println( "Client time: " + format( clientTime))
    clientTime.getMillis - serverTime.getMillis
  }

  private def parse(timeStr: String): DateTime = {
    var result: DateTime = null
    if (!isBlank(timeStr)) {
      try {
        result = MILLISECONDS_DATEFORMAT.parseDateTime(timeStr)
      }
      catch {
        case e: Exception =>
          println("ERROR: Exception, strToDate() can't parse <" + timeStr + ">, at time <" + new DateTime + ">")
      }
    }
    result
  }

  private def format(date: DateTime): String = {
    var result: String = null
    if (date != null) {
      result = date.formatted(MILLISECONDS_DATE_TIME_PARSE_STRING)
    }
    result
  }
}
