package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action.{FailureRequest, _}
import by.itechart.constant.{Constant, StateId}
import by.itechart.date.MyDate
import by.itechart.service.DatabaseService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class NormalizationActor(
                          private val ds: DatabaseService = new DatabaseService()
                        ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(Constant.TimeoutSec.seconds)

  def receive = {
    case message: RunNormalizationState =>
      ds.getFlowById(message.flowId, StateId.normalizationId.id).flatMap {
        case res: SuccessfulRequest =>
          message.statesToActor(StateId.validationId.id) ?
            PassToValidationState(res.flow.copy(statusId = StateId.validationId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureRequest => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToNormalizationState =>
      ds.insertNormalizationFlow(message.flow).flatMap {
        case res: SuccessfulRequest =>
          message.statesToActor(StateId.validationId.id) ?
            PassToValidationState(res.flow.copy(statusId = StateId.validationId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureRequest => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
  }
}
