package by.itechart.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.actor.state._
import by.itechart.enums.StateId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class SupervisorActor extends Actor with ActorLogging {
  implicit val timeout = Timeout(30.seconds)
  private val statesToActor: Map[Int, ActorRef] = Map(
    StateId.initializationId.id -> context.actorOf(Props(new InitializationActor()), name = "state-init"),
    StateId.startId.id -> context.actorOf(Props(new StartActor()), name = "state-start"),
    StateId.retrievalId.id -> context.actorOf(Props(new RetrievalActor()), name = "state-retrieve"),
    StateId.transformationId.id -> context.actorOf(Props(new TransformationActor()), name = "state-transform"),
    StateId.normalizationId.id -> context.actorOf(Props(new NormalizationActor()), name = "state-normalize"),
    StateId.validationId.id -> context.actorOf(Props(new ValidationActor()), name = "state-validate"),
    StateId.loadId.id -> context.actorOf(Props(new LoadActor()), name = "state-load"),
    StateId.finishId.id -> context.actorOf(Props(new FinishActor()), name = "state-finish"))

  def receive = {
    case _: CreateNewFlow =>
      (statesToActor(StateId.initializationId.id) ? RunInitializationState(statesToActor)).mapTo[Seq[Notice]].pipeTo(sender())
    case message: InitStartState =>
      (statesToActor(StateId.startId.id) ? RunStartState(message.flowId, statesToActor)).mapTo[Notice].pipeTo(sender())
    case message: InitRetrievalState =>
      (statesToActor(StateId.retrievalId.id) ? RunRetrievalState(message.flowId, statesToActor)).mapTo[Notice].pipeTo(sender())
    case message: InitTransformationState =>
      (statesToActor(StateId.transformationId.id) ? RunTransformationState(message.flowId, statesToActor)).mapTo[Notice].pipeTo(sender())
    case message: InitNormalizationState =>
      (statesToActor(StateId.normalizationId.id) ? RunNormalizationState(message.flowId, statesToActor)).mapTo[Notice].pipeTo(sender())
    case message: InitValidationState =>
      (statesToActor(StateId.validationId.id) ? RunValidationState(message.flowId, statesToActor)).mapTo[Notice].pipeTo(sender())
    case message: InitLoadState =>
      (statesToActor(StateId.loadId.id) ? RunLoadState(message.flowId, statesToActor)).mapTo[Notice].pipeTo(sender())
    case message: InitFinishState =>
      (statesToActor(StateId.finishId.id) ? RunFinishState(message.flowId, statesToActor)).mapTo[Notice].pipeTo(sender())
  }
}
