package by.itechart

import akka.actor.{ActorSystem, Props}
import by.itechart.action.{InitNormalizeState, InitRetrieveState, InitStartState}
import by.itechart.actor.SupervisorActor

object Application extends App {
  val system = ActorSystem("actor-system")
  val supervisor = system.actorOf(Props[SupervisorActor])
  supervisor ! InitStartState("20")
  supervisor ! InitNormalizeState("2")
  supervisor ! InitRetrieveState("18")
  Thread.sleep(5000)
  system.terminate()
}
