package by.itechart.action

import akka.actor.ActorRef
import by.itechart.dao.{Flow, Retrieval}

sealed trait Message

case class CreateNewFlow() extends Message

case class InitStartState(flowId: String) extends Message

case class InitRetrievalState(flowId: String) extends Message

case class InitTransformationState(flowId: String) extends Message

case class InitNormalizationState(flowId: String) extends Message

case class InitValidationState(flowId: String) extends Message

case class InitLoadState(flowId: String) extends Message

case class InitFinishState(flowId: String) extends Message

case class RunInitializationState(statesToActor: Map[Int, ActorRef]) extends Message

case class RunStartState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunRetrievalState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunTransformationState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunNormalizationState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunValidationState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunLoadState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunFinishState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToStartState(fileName: String, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToRetrievalState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToTransformationState(flow: Retrieval, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToNormalizationState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToValidationState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToLoadState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToFinishState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message