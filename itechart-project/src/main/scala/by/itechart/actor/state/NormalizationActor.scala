package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action.{FailureRequest, _}
import by.itechart.constant.{Constant, StateId}
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
      ds.getNormalizationFlowById(message.flowId).flatMap {
        case res: SuccessfulRequestForNormalization =>
          message.statesToActor(StateId.validationId.id) ?
            PassToValidationState(res.flow.toList, message.statesToActor)
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
    case message: RunNormalizationStateByKeys =>
      ds.getNormalizationFlowByKeys(message.flowId, message.companyName, message.departmentName, message.payDate).flatMap {
        case res: SuccessfulRequestForNormalization =>
          message.statesToActor(StateId.validationId.id) ?
            PassToValidationState(res.flow.toList, message.statesToActor)
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToNormalizationState =>
      ds.insertNormalizationFlow(message.flow).flatMap {
        case res: SuccessfulRequestForNormalization =>
          message.statesToActor(StateId.validationId.id) ?
            PassToValidationState(res.flow.toList, message.statesToActor)
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
  }
}
