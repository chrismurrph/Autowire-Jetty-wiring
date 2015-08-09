package wiring

import akka.actor.{Props, ActorSystem}
import spray.servlet.WebBoot

/**
 * application.conf instantiates this (see WEB-INF/classes)
 */ 
class Boot extends WebBoot {

  val systemName = "WIRING_GITHUB_EXAMPLE"
  println("Now booting " + systemName)
  val system = ActorSystem(systemName)

  // the service actor replies to incoming HttpRequests
  val serviceActor = system.actorOf(Props(new ExampleServiceActor()), 
    name = "WiringExample")

}