package by.itechart

import akka.actor.{ActorSystem, Props}
import by.itechart.action.{InitStartState, InitTransformationState}
import by.itechart.actor.SupervisorActor

object Application extends App {
  val system = ActorSystem("actor-system")
  val supervisor = system.actorOf(Props[SupervisorActor])
  supervisor ! InitStartState("1")
  supervisor ! InitTransformationState("2")
  Thread.sleep(2000)
  system.terminate()
}
