package by.itechart.action

import akka.actor.ActorRef

sealed trait Message

case class InitStartState(flowId: String) extends Message

case class InitRetrievalState(flowId: String) extends Message

case class InitTransformationState(flowId: String) extends Message

case class InitNormalizationState(flowId: String) extends Message

case class InitValidationState(flowId: String) extends Message

case class InitLoadingState(flowId: String) extends Message

case class InitFinishState(flowId: String) extends Message

case class RunStartState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunRetrievalState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunTransformationState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunNormalizationState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunValidationState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunLoadingState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunFinishState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class SuccessfulMessage(val message: String = "Request was completed!!!") extends Message

case class FailureMessage(val message: String = "Error!!!") extends Message