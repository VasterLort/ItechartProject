package by.itechart

import akka.actor.{ActorSystem, Props}
import by.itechart.action._
import by.itechart.actor.SupervisorActor

object Application extends App {
  val system = ActorSystem("actor-system")
  val supervisor = system.actorOf(Props[SupervisorActor])
  supervisor ! StateStart("1")
  supervisor ! StateTransform("2")
  Thread.sleep(2000)
  system.terminate()
}
