package wiring

import upickle._
import akka.actor.SupervisorStrategy.Restart
import akka.actor.{OneForOneStrategy, Actor, Props, ActorRef}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import spray.http.{MediaTypes, HttpEntity}
import spray.routing._

object Template{
  import scalatags.Text.all._
  import scalatags.Text.tags2.title
  val txt =
    "<!DOCTYPE html>" +
    html(
      head(
        title("Example Scala.js application"),
        meta(httpEquiv:="Content-Type", content:="text/html; charset=UTF-8"),
        script(`type`:="text/javascript", src:="/" + Constants.WebAppDirName + "/client-fastopt.js")
      ),
      body(margin:=0)(
        script("wiring.ScalaJSExample().main()")
      )
    )
}

object AutowireServer extends autowire.Server[String, upickle.default.Reader, upickle.default.Writer]{
  def read[Result: upickle.default.Reader](p: String) = upickle.default.read[Result](p)
  def write[Result: upickle.default.Writer](r: Result) = upickle.default.write(r)
}

trait ExampleService extends HttpService with Api {
  self: ExampleService =>
  
  def readStr(str: String) = {println( "READ: " + str);str}  
  
  val exampleRoute =
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
    /*
    post {
      path(Segments) {
        case s@(head :: tail) if head == "remoting" => pr("rejecting: " + s); reject
        case s =>
          extract(_.request.entity.asString) { clientReqStr =>
            complete {
              Router.route[AtApi](self)(
                autowire.Core.Request(s, upickle.default.read[Map[String, String]](readStr(clientReqStr)))
              )
            }
          }
      }
    }
    */
  def list(path: String) = {
    val chunks = path.split("/", -1)
    val prefix = "./" + chunks.dropRight(1).mkString("/")
    val files = Option(new java.io.File(prefix).list()).toSeq.flatten
    files.filter(_.startsWith(chunks.last))
  }    
}

class ExampleServiceActor extends Actor with ExampleService {
  def actorRefFactory = context
  def receive = runRoute(exampleRoute)
}

/*
object Server extends SimpleRoutingApp with Api{
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    startServer("0.0.0.0", port = 8080) {
      get{
        pathSingleSlash {
          complete{
            HttpEntity(
              MediaTypes.`text/html`,
              Template.txt
            )
          }
        } ~
        getFromResourceDirectory("")
      } ~
      post {
        path("api" / Segments){ s =>
          extract(_.request.entity.asString) { e =>
            complete {
              AutowireServer.route[Api](Server)(
                autowire.Core.Request(s, upickle.default.read[Map[String, String]](e))
              )
            }
          }
        }
      }
    }
  }
}
*/
