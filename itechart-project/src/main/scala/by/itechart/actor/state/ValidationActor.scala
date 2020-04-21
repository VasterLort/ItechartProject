package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.constant.{Constant, StateId}
import by.itechart.service.{DatabaseService, ErrorService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class ValidationActor(
                       private val ds: DatabaseService = new DatabaseService,
                       private val errorService: ErrorService = new ErrorService
                     ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(Constant.TimeoutSec.seconds)

  def receive = {
    case message: RunValidationState =>
      ds.getValidationFlowById(message.flowId).flatMap {
        case res: SuccessfulRequestForValidation =>
          val response = message.statesToActor(StateId.loadId.id) ? PassToLoadState(res.flow.toList, message.statesToActor)
          response.map {
            case notice: FailureLoading =>
              NotEmptyFolderFailure(errorService.getMergedErrors(Seq(notice)))
            case _ =>
              NotEmptyFolderSuccessful(errorService.getSuccessfulResult(Constant.Successfully))
          }
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
    case message: RunValidationStateByKeys =>
      ds.getValidationFlowByKeys(message.flowId, message.companyName, message.departmentName, message.payDate).flatMap {
        case res: SuccessfulRequestForValidation =>
          val response = message.statesToActor(StateId.loadId.id) ? PassToLoadState(res.flow.toList, message.statesToActor)
          response.map {
            case notice: FailureLoading =>
              NotEmptyFolderFailure(errorService.getMergedErrors(Seq(notice)))
            case _ =>
              NotEmptyFolderSuccessful(errorService.getSuccessfulResult(Constant.Successfully))
          }
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToValidationState =>
      ds.insertValidationFlow(message.flow).flatMap {
        case res: SuccessfulRequestForValidation =>
          message.statesToActor(StateId.loadId.id) ? PassToLoadState(res.flow.toList, message.statesToActor)
        case notice: FailureValidationList => Future.successful(notice)
      }.mapTo[Notice].pipeTo(sender())
  }
}
