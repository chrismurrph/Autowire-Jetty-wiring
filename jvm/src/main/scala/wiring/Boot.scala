package wiring

import akka.actor.{Props, ActorSystem}
// Keep generic until up on GitHub
// import com.seasoft.server.applic.RemoteLocalActorConstants
// import RemoteLocalActorConstants._
import spray.servlet.WebBoot

// this class is instantiated by the servlet initializer
// it needs to have a default constructor and implement
// the spray.servlet.WebBoot trait
class Boot extends WebBoot {

  val systemName = "WIRING_GITHUB_EXAMPLE"
  println("Now booting " + systemName)
  // we need an ActorSystem to host our application in
  val system = ActorSystem(systemName)

  // the service actor replies to incoming HttpRequests
  val serviceActor = system.actorOf(Props(new ExampleServiceActor()), 
    name = "WiringExample")

}