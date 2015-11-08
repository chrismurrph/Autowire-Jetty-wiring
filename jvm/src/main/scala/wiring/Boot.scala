package wiring

import akka.actor.{Props, ActorSystem}
import com.seasoft.atmosphere.RemoteLocalActorConstants
import RemoteLocalActorConstants._
import spray.servlet.WebBoot

/**
 * application.conf instantiates this (see WEB-INF/classes)
 */
class Boot extends WebBoot {

  println("Now booting " + SystemName)
  // we need an ActorSystem to host our application in
  val system = ActorSystem(SystemName)

  // the service actor replies to incoming HttpRequests
  val serviceActor = system.actorOf(Props(new TrendingServiceActor()),
    name = WiringInLocalActorName)

}