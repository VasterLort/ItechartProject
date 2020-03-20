package by.itechart.action

import akka.actor.ActorRef
import by.itechart.dao.Flow

sealed trait Message

case class InitStartState(flowId: String) extends Message

case class InitRetrieveState(flowId: String) extends Message

case class InitTransformState(flowId: String) extends Message

case class InitNormalizeState(flowId: String) extends Message

case class InitValidateState(flowId: String) extends Message

case class InitLoadState(flowId: String) extends Message

case class RunStartState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunRetrieveState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunTransformState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunNormalizeState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunValidateState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunLoadState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToRetrieveState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToTransformState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToNormalizeState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToValidateState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToLoadState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToFinishState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message