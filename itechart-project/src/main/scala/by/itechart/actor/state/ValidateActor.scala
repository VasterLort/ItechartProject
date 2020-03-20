package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout
import by.itechart.action._
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DatabaseService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class ValidateActor(
                     private val ds: DatabaseService = new DatabaseService
                   ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: RunValidateState =>
      ds.getFlowById(message.flowId, StateId.validateId.id).map {
        case res: SuccessfulNotice =>
          (message.statesToActor(StateId.loadId.id) ?
            PassToLoadState(res.flow.copy(statusId = StateId.loadId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor))
            .mapTo[Notice]
        case res: FailureNotice => res
      }
    case message: PassToValidateState =>
      ds.insertFlow(message.flow).map {
        case res: SuccessfulNotice =>
          (message.statesToActor(StateId.loadId.id) ?
            PassToLoadState(res.flow.copy(statusId = StateId.loadId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor))
            .mapTo[Notice]
        case res: FailureNotice => res
      }
  }
}
