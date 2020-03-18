package by.itechart.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import by.itechart.action._
import by.itechart.actor.state._
import by.itechart.enums.StateId

class SupervisorActor extends Actor with ActorLogging {
  private val statesToActor: Map[Int, ActorRef] = Map(
    StateId.startId.id -> context.actorOf(Props(new Start()), name = "state-start"),
    StateId.retrieveId.id -> context.actorOf(Props(new Retrieval()), name = "state-retrieve"),
    StateId.transformId.id -> context.actorOf(Props(new Transformation()), name = "state-transform"),
    StateId.normalizeId.id -> context.actorOf(Props(new Normalization()), name = "state-normalize"),
    StateId.validateId.id -> context.actorOf(Props(new Validation()), name = "state-validate"),
    StateId.loadId.id -> context.actorOf(Props(new Loading()), name = "state-load"),
    StateId.finishId.id -> context.actorOf(Props(new Finish()), name = "state-finish"))

  def receive = {
    case message: InitStartState => statesToActor(StateId.startId.id) ! RunStartState(message.flowId, statesToActor)
    case message: InitRetrievalState => statesToActor(StateId.retrieveId.id) ! RunRetrievalState(message.flowId, statesToActor)
    case message: InitTransformationState => statesToActor(StateId.transformId.id) ! RunTransformationState(message.flowId, statesToActor)
    case message: InitNormalizationState => statesToActor(StateId.normalizeId.id) ! RunNormalizationState(message.flowId, statesToActor)
    case message: InitValidationState => statesToActor(StateId.validateId.id) ! RunValidationState(message.flowId, statesToActor)
    case message: InitLoadingState => statesToActor(StateId.loadId.id) ! RunLoadingState(message.flowId, statesToActor)
    case message: InitFinishState => statesToActor(StateId.finishId.id) ! RunFinishState(message.flowId, statesToActor)
  }
}
