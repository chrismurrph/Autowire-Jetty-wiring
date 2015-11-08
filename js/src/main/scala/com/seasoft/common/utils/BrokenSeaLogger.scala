package com.seasoft.common.utils

import java.util.Date

/**
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
object BrokenSeaLogger {
  val MILLISECONDS_DATE_TIME_PARSE_STRING = "dd_MM_yyyy__HH_mm_ss.SSS"
  //val MILLISECONDS_DATEFORMAT: DateFormat = new SimpleDateFormat(MILLISECONDS_DATE_TIME_PARSE_STRING)
  //Not yet implemented by Sebastien
  //private val TZ = TimeZone.getTimeZone("Australia/Sydney")
  //MILLISECONDS_DATEFORMAT.setTimeZone(TZ)

  def isBlank(str:String): Boolean = {
    str == null || "" == str
  }

  private def nowOnServersTZ: Date = {
    /*
    val calendar: Calendar = new GregorianCalendar()
    //Not yet implemented by Sebastien
    calendar.setTimeZone(BrokenSeaLogger.TZ)
    val clientTime:Date = calendar.getTime
    clientTime
    */
    new Date
  }

  def getClientFastBy(serverTimeStr: String): Long = {
    val serverTime:Date = parse( serverTimeStr)
    val clientTime:Date = nowOnServersTZ
    println( "Client time: " + format( clientTime))
    clientTime.getTime - serverTime.getTime
  }

  private def parse(timeStr: String): Date = {
    var result: Date = null
    /*
    if (!isBlank(timeStr)) {
      try {
        result = MILLISECONDS_DATEFORMAT.parse(timeStr)
      }
      catch {
        case e: Exception =>
          println("ERROR: Exception, strToDate() can't parse <" + timeStr + ">, at time <" + new Date + ">")
      }
    }
    */
    result = new Date
    result
  }

  private def format(date: Date): String = {
    var result: String = null
    /*
    if (date != null) {
      result = MILLISECONDS_DATEFORMAT.format(date)
    }
    */
    result
  }
}
