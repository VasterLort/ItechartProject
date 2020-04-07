package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.constant.{Constant, StateId}
import by.itechart.date.MyDate
import by.itechart.service.DatabaseService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class ValidationActor(
                       private val ds: DatabaseService = new DatabaseService
                     ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(Constant.TimeoutSec.seconds)

  def receive = {
    case message: RunValidationState =>
      ds.getFlowById(message.flowId, StateId.validationId.id).flatMap {
        case res: SuccessfulRequest =>
          message.statesToActor(StateId.loadId.id) ?
            PassToLoadState(res.flow.copy(statusId = StateId.loadId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureRequest => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToValidationState =>
      ds.insertFlow(message.flow).flatMap {
        case res: SuccessfulRequest =>
          message.statesToActor(StateId.loadId.id) ?
            PassToLoadState(res.flow.copy(statusId = StateId.loadId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureRequest => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
  }
}
