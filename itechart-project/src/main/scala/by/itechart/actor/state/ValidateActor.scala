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

class ValidateActor(
                     private val ds: DatabaseService = new DatabaseService
                   ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: RunValidateState =>
      ds.getFlowById(message.flowId, StateId.validateId.id).flatMap { result =>
        if (result.isInstanceOf[SuccessfulNotice]) {
          (message.statesToActor(StateId.loadId.id) ?
            PassToLoadState(
              result.asInstanceOf[SuccessfulNotice].flow.copy(statusId = StateId.loadId.id, statusDate = MyDate.getCurrentDate()),
              message.statesToActor))
            .mapTo[Notice]
        } else Future.successful(result.asInstanceOf[FailureNotice])
      }
    case message: PassToValidateState =>
      ds.insertFlow(message.flow).flatMap { result =>
        if (result.isInstanceOf[SuccessfulNotice]) {
          (message.statesToActor(StateId.loadId.id) ?
            PassToLoadState(
              result.asInstanceOf[SuccessfulNotice].flow.copy(statusId = StateId.loadId.id, statusDate = MyDate.getCurrentDate()),
              message.statesToActor))
            .mapTo[Notice]
        } else Future.successful(result.asInstanceOf[FailureNotice])
      }
  }
}
