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

class LoadActor(
                 private val ds: DatabaseService = new DatabaseService
               ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(120.seconds)

  def receive = {
    case message: RunLoadState =>
      ds.getFlowById(message.flowId, StateId.loadId.id).flatMap {
        case res: SuccessfulRequest =>
          message.statesToActor(StateId.finishId.id) ?
            PassToFinishState(res.flow.copy(statusId = StateId.finishId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureRequest => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToLoadState =>
      ds.insertFlow(message.flow).flatMap {
        case res: SuccessfulRequest =>
          message.statesToActor(StateId.finishId.id) ?
            PassToFinishState(res.flow.copy(statusId = StateId.finishId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureRequest => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
  }
}
