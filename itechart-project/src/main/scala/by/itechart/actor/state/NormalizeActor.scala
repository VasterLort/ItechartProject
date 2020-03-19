package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout
import by.itechart.action._
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DatabaseService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class NormalizeActor(
                      private val ds: DatabaseService = new DatabaseService
                    ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: RunNormalizeState =>
      ds.getFlowById(message.flowId, StateId.normalizeId.id).flatMap { result =>
        if (result.isInstanceOf[SuccessfulNotice]) {
          (message.statesToActor(StateId.validateId.id) ?
            PassToValidateState(
              result.asInstanceOf[SuccessfulNotice].flow.copy(statusId = StateId.validateId.id, statusDate = MyDate.getCurrentDate()),
              message.statesToActor))
            .mapTo[Notice]
        } else Future.successful(result.asInstanceOf[FailureNotice])
      }
    case message: PassToNormalizeState =>
      ds.insertFlow(message.flow).flatMap { result =>
        if (result.isInstanceOf[SuccessfulNotice]) {
          (message.statesToActor(StateId.validateId.id) ?
            PassToValidateState(
              result.asInstanceOf[SuccessfulNotice].flow.copy(statusId = StateId.validateId.id, statusDate = MyDate.getCurrentDate()),
              message.statesToActor))
            .mapTo[Notice]
        } else Future.successful(result.asInstanceOf[FailureNotice])
      }
  }
}
