package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.constant.{Constant, StateId}
import by.itechart.dao.Flow
import by.itechart.date.MyDate
import by.itechart.service.DatabaseService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class LoadActor(
                 private val ds: DatabaseService = new DatabaseService
               ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(Constant.TimeoutSec.seconds)

  def receive = {
    case message: RunLoadState =>
      ds.getLoadFlowById(message.flowId).flatMap {
        case res: SuccessfulRequestForLoad =>
          message.statesToActor(StateId.finishId.id) ?
            PassToFinishState(Flow(res.flow(Constant.StartIndex).flowId, res.flow(Constant.StartIndex).fileName, StateId.finishId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToLoadState =>
      ds.insertLoadFlow(message.flow).flatMap {
        case res: SuccessfulRequestForLoad =>
          message.statesToActor(StateId.finishId.id) ?
            PassToFinishState(Flow(res.flow(Constant.StartIndex).flowId, res.flow(Constant.StartIndex).fileName, StateId.finishId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
  }
}
