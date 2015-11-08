package wiring

import java.util.Date

import com.cmts.common.business.CMTSConstants
import com.cmts.common.service.{TimeServiceI, ServerSessionI}
import com.seasoft.alarmer.common.servicedoi._
import com.seasoft.atmosphere.Gas
import com.seasoft.common.utils.{SeaLogger, StopWatch}
import com.seasoft.common.view.LoginAction
import upickle._
import akka.actor.SupervisorStrategy.Restart
import akka.actor.{OneForOneStrategy, Actor, Props, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.{MediaTypes, HttpEntity}
import spray.routing._
import scala.concurrent.duration._
import java.util.{List => JList, Date, ArrayList => JArrayList}

object Template{
  import scalatags.Text.all._
  import scalatags.Text.tags2.title
  val startScript = "<script>goog.require(\"trending.core\");</script>"
  val txt =
    "<!DOCTYPE html>" +
    html(
      head(
        title("Example Scala.js application"),
        meta(httpEquiv:="Content-Type", content:="text/html; charset=UTF-8"),
        script(`type`:="text/javascript", src:="js/react.0.9.0.js"),
        meta(name:="viewport", content:="width=device-width, initial-scale=0.67, maximum-scale=0.67, user-scalable=no"),
        link(href:="css/style.css", rel:="stylesheet", `type`:="text/css")
      ),
      body(
        div( `class`:="container",
          div( id:="board-area",
            /*
             * Only this root id is important as it is used by Javascript
             */
            div( id:="root")
          )
        ),
        script( `type`:="text/javascript", src:="/" + Constants.WebAppDirName + "/client-fastopt.js"),
        script( `type`:="text/javascript", src:="js/out/goog/base.js"),
        script( `type`:="text/javascript", src:="js/trending.js"),
        raw(startScript)
      )
    )
}

object AutowireServer extends autowire.Server[String, upickle.default.Reader, upickle.default.Writer]{
  def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)
  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}

trait TrendingQueryService extends HttpService with Api {
  self: TrendingQueryService =>
  
  def readStr(str: String) = {println( "READ: " + str);str}  
  
  val outfacingRoute =
    get {
      pathSingleSlash {
        complete {
          HttpEntity(
            MediaTypes.`text/html`,
            Template.txt
          )
        }
      } ~
      getFromResourceDirectory("") 
    } ~
    post {
      path(Segments){
        case s@(head :: tail) if head != "wiring" => println("rejecting: " + s); reject
        case s =>
          extract(_.request.entity.asString) { clientReqStr =>
            complete {
              AutowireServer.route[Api](self)(
                autowire.Core.Request(s, upickle.default.read[Map[String, String]](readStr(clientReqStr)))
              )
            }
          }
      }
    }

  def services: Option[SmartgasServerServices]
  def list(path: String) = {
    val chunks = path.split("/", -1)
    val prefix = "./" + chunks.dropRight(1).mkString("/")
    val files = Option(new java.io.File(prefix).list()).toSeq.flatten
    files.filter(_.startsWith(chunks.last))
  }

  def browserLogon(s: String): Option[String] = {
    services.map( ser => retrieveTime(ser))
  }

  //def testRequestGraphLine()
  def retrieveTime(serverServices: SmartgasServerServices):String
  def requestGraphLine(startTimeStr: String,
                       endTimeStr: String,
                       physicalSubstanceNames: Seq[String],
                       displayName: String,
                       displayTimeStr: String):Map[String,List[GraphPoint]]
}

private case object TickMessage

class TrendingServiceActor extends Actor with TrendingQueryService {
  def actorRefFactory = context
  def receive = handleOthers() orElse runRoute(outfacingRoute)
  case class LoginInfo(loginOpt:Option[LoginAction]=None, count:Int=0)
  var loginInfo:LoginInfo = LoginInfo()
  var services:Option[SmartgasServerServices] = None
  var startupInfo: SPInfosDOI = null
  //var clientFastBy: Long = 0L

  private def haveConnection = loginInfo.loginOpt.fold(false){lA => lA.isOkLogin}

  private def login():Option[LoginAction] = {
    println(s"LOGGING in for all browser users")
    val sgInfo = new SmartgasInfo
    val res = new UsersConnecting(sgInfo).connect()
    if(res.isOkLogin) {
      println(s"Succeeded to connect (this time) using: $sgInfo")
      services = Some(new SmartgasServerServices(sgInfo))
    } else {
      println(s"WARNING: Failed to connect (this time) using: $sgInfo")
    }
    Some(res)
  }

  def retrieveTime(sers: SmartgasServerServices):String =
  {
    val timer:StopWatch  = new StopWatch( "Call Server to find server-time")
    timer.startTiming()
    val timeStr:String = sers.requestTime
    timer.stopTiming()
    println( "Call took: " + timer.getResult)
    println( "Server time str: " + timeStr)
    //val serverTime:Date = SeaLogger.strToDate( timeStr)
    //println( "Server time " + timeStr)
    /* For Client to know
    val clientTime:Date = new Date()
    println( "Client time " + SeaLogger.format( clientTime))
    clientFastBy = clientTime.getTime - serverTime.getTime
    println( "Client fast by " + clientFastBy)
    println( "VERSION at: " + CMTSConstants.VERSION)
    */
    timeStr
  }

  def requestGraphLine(startTimeStr: String,
                       endTimeStr: String,
                       physicalSubstanceNames: Seq[String],
                       displayName: String,
                       displayTimeStr: String):Map[String,List[GraphPoint]] =
  {
    import scala.collection.JavaConverters._
    def convertList(in:JList[String]): List[String] = in.asScala.toList
    def convertOldGraphPoint(gasName:String, gP:GraphPointDOI): GraphPoint = {
      //Formatting for the gas type so Client doesn't have to worry about it
      val gas:Option[Gas] = Gas.withName(gasName.replace('_',' '))
      println(s"Going to map from: $gP, has type: ${gP.getType}, and is of gas: $gas")
      def toStr(value:BigDecimal):String = {
        if(value != null) {gas.fold(null:String){g => g.formatGasVal(value.asInstanceOf[BigDecimal].toFloat)}} else null
      }
      GraphPoint(toStr(gP.getMinVal.asScala),toStr(gP.getMaxVal.asScala),toStr(gP.getAvgVal.asScala),
        gP.getSampleTimeStr,gP.getMinValTimeStr,gP.getMaxValTimeStr,gP.getType.getName)
    }
    if(haveConnection) {
      val inRes:MultigasDOI = services.get.requestGraphLine(startTimeStr,endTimeStr,
        physicalSubstanceNames,displayName,displayTimeStr,loginInfo.loginOpt.get.getSessionId)
      val inGasNames:JList[String] = inRes.getGasNamesList
      val gasNamesWithIdx:List[(String,Int)] = convertList(inGasNames).zipWithIndex
      val graphLinesPerGas:List[GraphLineDOI] = gasNamesWithIdx.map( gasName => { inRes.getGraphLine(gasName._1)})
      val graphPointsPerGas:List[List[GraphPointDOI]] = graphLinesPerGas.map( graphLine => { Range(0, graphLine.size-1).map(i => graphLine.getGraphPoint(i))}.toList)
      val res: Map[String,List[GraphPoint]] = (for(
        gasNameIdx <- gasNamesWithIdx
      ) yield (gasNameIdx._1 -> graphPointsPerGas(gasNameIdx._2).map(graphPointDOI => convertOldGraphPoint(gasNameIdx._1, graphPointDOI)))).toMap
      res
    } else {
      Map[String,List[GraphPoint]]()
    }
  }

  /*
  def testRequestGraphLine() = {
    if(haveConnection) {
      val res:MultigasDOI = services.get.requestGraphLine("25_08_2015__17_45_27.061","25_08_2015__18_45_27.061",
        List("Carbon_Monoxide","Carbon_Dioxide"),"Shed Tube 10","25_08_2015__18_45_27.273",loginInfo.loginOpt.get.getSessionId)
      println(s"RES: ${res.toString}")
    }
  }
  */

  private def handleOthers(): Receive = {
    case TickMessage =>
      //println("---------> TICK in TrendingServiceActor")
      if(!haveConnection){
        loginInfo = LoginInfo(login(), count = 1)
        if(haveConnection) {
          startupInfo = services.get.requestStartupInfo.getSpInfos
          println("INFO: " + startupInfo)
          //Done by each user
          //retrieveTime
        }
      }
  }

  def repeatedTick(tickActor: ActorRef) = {
    context.system.scheduler.schedule(5.seconds,
      10.seconds,
      tickActor,
      TickMessage)
  }

  /*
   * Frequently self a TickMessage
   */
  repeatedTick(self)
}
