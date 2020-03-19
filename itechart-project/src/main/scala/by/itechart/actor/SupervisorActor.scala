package by.itechart.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import by.itechart.action._
import by.itechart.actor.state._
import by.itechart.enums.StateId

import scala.concurrent.duration._

class SupervisorActor extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)
  private val statesToActor: Map[Int, ActorRef] = Map(
    StateId.startId.id -> context.actorOf(Props(new StartActor()), name = "state-start"),
    StateId.retrieveId.id -> context.actorOf(Props(new RetrieveActor()), name = "state-retrieve"),
    StateId.transformId.id -> context.actorOf(Props(new TransformActor()), name = "state-transform"),
    StateId.normalizeId.id -> context.actorOf(Props(new NormalizeActor()), name = "state-normalize"),
    StateId.validateId.id -> context.actorOf(Props(new ValidateActor()), name = "state-validate"),
    StateId.loadId.id -> context.actorOf(Props(new LoadActor()), name = "state-load"),
    StateId.finishId.id -> context.actorOf(Props(new FinishActor()), name = "state-finish"))

  def receive = {
    case message: InitStartState => (statesToActor(StateId.startId.id) ? RunStartState(message.flowId, statesToActor)).mapTo[Notice]
    case message: InitRetrieveState => (statesToActor(StateId.retrieveId.id) ? RunRetrieveState(message.flowId, statesToActor)).mapTo[Notice]
    case message: InitTransformState => (statesToActor(StateId.transformId.id) ? RunTransformState(message.flowId, statesToActor)).mapTo[Notice]
    case message: InitNormalizeState => (statesToActor(StateId.normalizeId.id) ? RunNormalizeState(message.flowId, statesToActor)).mapTo[Notice]
    case message: InitValidateState => (statesToActor(StateId.validateId.id) ? RunValidateState(message.flowId, statesToActor)).mapTo[Notice]
    case message: InitLoadState => (statesToActor(StateId.loadId.id) ? RunLoadState(message.flowId, statesToActor)).mapTo[Notice]
  }
}
