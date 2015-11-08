package wiring

import com.seasoft.common.utils.BrokenSeaLogger

import scala.scalajs.js
import org.scalajs.dom
import scala.concurrent.Future
import scala.scalajs.js.annotation.JSExport
import scalajs.concurrent.JSExecutionContext.Implicits.runNow
import autowire._

object Client extends autowire.Client[String, upickle.default.Reader, upickle.default.Writer]{
  override def doCall(req: Request): Future[String] = {
    dom.ext.Ajax.post(
      url = "/" + Constants.WebAppDirName + "/" + req.path.mkString("/"),
      data = upickle.default.write(req.args)
    ).map(_.responseText)
  }

  def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)
  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}


@JSExport
object ServerFacing {
  var clientFastBy = 0L
  def callList(s:String): Unit = {
    Client[Api].list(s).call().foreach{ paths =>
      paths.foreach( path => {
        //println(path)
        js.Dynamic.global.trending.core.successful_browse_filesystem_call(path)
      })
    }
  }
  def logon(): Unit = {
    Client[Api].browserLogon("").call().foreach{ serverTimeStrOpt =>
      serverTimeStrOpt.foreach( serverTimeStr => {
        clientFastBy = BrokenSeaLogger.getClientFastBy(serverTimeStr)
        println( s"Client fast by: $clientFastBy, from returned server time String: $serverTimeStr")
        //js.Dynamic.global.trending.core.successful_time_call(serverTimeStr)
      })
    }
  }
  /*
   * Relying on the fact that there's only ever one filled in
   */
  def aggregateType(gasPoint:GraphPoint): (String, String) = {
    if(gasPoint.avg == null) {
      if(gasPoint.max == null) {
        ("min",gasPoint.min)
      } else {
        ("max",gasPoint.max)
      }
    } else {
      ("avg",gasPoint.avg)
    }
  }

  def dateOfValue(gasPoint:GraphPoint): String = {
    if(gasPoint.sampleTime == null) {
      if(gasPoint.maxValTime == null) {
        gasPoint.minValTime
      } else {
        gasPoint.maxValTime
      }
    } else {
      gasPoint.sampleTime
    }
  }

  def periodTypeOfValue(gasPoint:GraphPoint): String = {
    gasPoint.periodType match{
      case "Monthly" => "monthly"
      case "Weekly" => "weekly"
      case "TenMinutely" => "ten-minutely"
      case "Minutely" => "minutely"
    }
  }

  def requestGraphLine(startTimeStr: String,
                       endTimeStr: String,
                       physicalSubstanceNames: Vector[String],
                       displayName: String,
                       displayTimeStr: String):Unit = {
    println(s"requestGraphLine (on Server) to be called with $physicalSubstanceNames")
    val fromServer = Client[Api].requestGraphLine(startTimeStr,endTimeStr,physicalSubstanceNames,displayName,displayTimeStr).call()
    fromServer.foreach(fromSer => {
      println(s"GRAPH POINT: $fromSer")
      fromSer.foreach( tup => {
        val gasName:String = tup._1
        val graphPoints:List[GraphPoint] = tup._2
        graphPoints.foreach( gp => {
          val aggregateNameVal = aggregateType(gp)
          js.Dynamic.global.trending.core.successful_graph_point_call(gasName, aggregateNameVal._1, aggregateNameVal._2, dateOfValue(gp), periodTypeOfValue(gp))
        })
      })
    })
  }
}
