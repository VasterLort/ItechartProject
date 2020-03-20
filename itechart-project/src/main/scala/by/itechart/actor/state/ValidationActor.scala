package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DatabaseService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class ValidationActor(
                     private val ds: DatabaseService = new DatabaseService
                   ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: RunValidationState =>
      ds.getFlowById(message.flowId, StateId.validationId.id).flatMap {
        case res: SuccessfulNotice =>
          message.statesToActor(StateId.loadId.id) ?
            PassToLoadState(res.flow.copy(statusId = StateId.loadId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureNotice => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToValidationState =>
      ds.insertFlow(message.flow).flatMap {
        case res: SuccessfulNotice =>
          message.statesToActor(StateId.loadId.id) ?
            PassToLoadState(res.flow.copy(statusId = StateId.loadId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureNotice => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
  }
}
