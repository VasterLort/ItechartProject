package by.itechart.action

import akka.actor.ActorRef
import by.itechart.dao._

sealed trait Message

case class CreateNewFlow() extends Message

case class InitStartState(flowId: String) extends Message

case class InitRetrievalState(flowId: String) extends Message

case class InitTransformationState(flowId: String) extends Message

case class InitNormalizationState(flowId: String) extends Message

case class InitValidationState(flowId: String) extends Message

case class InitLoadState(flowId: String) extends Message

case class InitFinishState(flowId: String) extends Message

case class InitTransformationStateByKeys(flowId: String, companyName: String, departmentName: String, payDate: String) extends Message

case class InitNormalizationStateByKeys(flowId: String, companyName: String, departmentName: String, payDate: String) extends Message

case class InitValidationStateByKeys(flowId: String, companyName: String, departmentName: String, payDate: String) extends Message

case class RunInitializationState(statesToActor: Map[Int, ActorRef]) extends Message

case class RunStartState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunRetrievalState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunTransformationState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunNormalizationState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunValidationState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunLoadState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunFinishState(flowId: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunTransformationStateByKeys(flowId: String, companyName: String, departmentName: String, payDate: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunNormalizationStateByKeys(flowId: String, companyName: String, departmentName: String, payDate: String, statesToActor: Map[Int, ActorRef]) extends Message

case class RunValidationStateByKeys(flowId: String, companyName: String, departmentName: String, payDate: String, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToStartState(fileName: String, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToRetrievalState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToTransformationState(flow: Retrieval, statesToActor: Map[Int, ActorRef]) extends Message

case class PassToNormalizationState(flow: List[Transformation], statesToActor: Map[Int, ActorRef]) extends Message

case class PassToValidationState(flow: List[Normalization], statesToActor: Map[Int, ActorRef]) extends Message

case class PassToLoadState(flow: List[Validation], statesToActor: Map[Int, ActorRef]) extends Message

case class PassToFinishState(flow: Flow, statesToActor: Map[Int, ActorRef]) extends Message