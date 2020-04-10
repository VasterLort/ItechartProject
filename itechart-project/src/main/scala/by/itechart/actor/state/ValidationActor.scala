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

class ValidationActor(
                       private val ds: DatabaseService = new DatabaseService
                     ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(Constant.TimeoutSec.seconds)

  def receive = {
    case message: RunValidationState =>
      ds.getValidationFlowById(message.flowId).flatMap {
        case res: SuccessfulRequestForValidation =>
          message.statesToActor(StateId.loadId.id) ?
            PassToLoadState(Flow(res.flow(Constant.StartIndex).flowId, res.flow(Constant.StartIndex).fileName, StateId.loadId.id, MyDate.getCurrentDate()), message.statesToActor)
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToValidationState =>
      ds.insertValidationFlow(message.flow).flatMap {
        case res: SuccessfulRequestForValidation =>
          message.statesToActor(StateId.loadId.id) ?
            PassToLoadState(Flow(res.flow(Constant.StartIndex).flowId, res.flow(Constant.StartIndex).fileName, StateId.loadId.id, MyDate.getCurrentDate()), message.statesToActor)
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
  }
}
