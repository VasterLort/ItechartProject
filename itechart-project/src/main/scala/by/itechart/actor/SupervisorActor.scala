package by.itechart.actor

import akka.actor.{Actor, ActorLogging, Props}
import by.itechart.action._
import by.itechart.actor.state._

class SupervisorActor extends Actor with ActorLogging {
  def receive = {
    case message: StateStart =>
      val ref = context.actorOf(Props(new Start()), name = message.flowId)
      ref ! message
    case message: StateRetrieve =>
      val ref = context.actorOf(Props(new Retrieve()), name = message.flowId)
      ref ! message
    case message: StateTransform =>
      val ref = context.actorOf(Props(new Transform()), name = message.flowId)
      ref ! message
    case message: StateNormalize =>
      val ref = context.actorOf(Props(new Normalize()), name = message.flowId)
      ref ! message
    case message: StateValidate =>
      val ref = context.actorOf(Props(new Validate()), name = message.flowId)
      ref ! message
    case message: StateLoad =>
      val ref = context.actorOf(Props(new Load()), name = message.flowId)
      ref ! message
    case message: StateFinish =>
      val ref = context.actorOf(Props(new Finish()), name = message.flowId)
      ref ! message
  }
}
