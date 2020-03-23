package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action.{FailureNotice, _}
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DatabaseService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class NormalizationActor(
                      private val ds: DatabaseService = new DatabaseService
                    ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: RunNormalizationState =>
      ds.getFlowById(message.flowId, StateId.normalizationId.id).flatMap {
        case res: SuccessfulNotice =>
          message.statesToActor(StateId.validationId.id) ?
            PassToValidationState(res.flow.copy(statusId = StateId.validationId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureNotice => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToNormalizationState =>
      ds.insertFlow(message.flow).flatMap {
        case res: SuccessfulNotice =>
          message.statesToActor(StateId.validationId.id) ?
            PassToValidationState(res.flow.copy(statusId = StateId.validationId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureNotice => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
  }
}
