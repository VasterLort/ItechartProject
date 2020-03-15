package by.itechart.actor

import akka.actor.{Actor, ActorLogging, Props}
import by.itechart.action._
import by.itechart.actor.state._

class SupervisorActor extends Actor with ActorLogging {
  def receive = {
    case message: StateStart =>
      val ref = context.actorOf(Props[Start], name = message.flowId)
      ref ! message
    case message: StateRetrieve =>
      val ref = context.actorOf(Props[Retrieve], name = message.flowId)
      ref ! message
    case message: StateTransform =>
      val ref = context.actorOf(Props[Transform], name = message.flowId)
      ref ! message
    case message: StateNormalize =>
      val ref = context.actorOf(Props[Normalize], name = message.flowId)
      ref ! message
    case message: StateValidate =>
      val ref = context.actorOf(Props[Validate], name = message.flowId)
      ref ! message
    case message: StateLoad =>
      val ref = context.actorOf(Props[Load], name = message.flowId)
      ref ! message
    case message: StateFinish =>
      val ref = context.actorOf(Props[Finish], name = message.flowId)
      ref ! message
  }
}
