package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action.{NotEmptyFolderFailure, _}
import by.itechart.constant.{Constant, StateId}
import by.itechart.service.{DatabaseService, ErrorService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class RetrievalActor(
                      private val ds: DatabaseService = new DatabaseService,
                      private val errorService: ErrorService = new ErrorService()
                    ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(Constant.TimeoutSec.seconds)

  def receive = {
    case message: RunRetrievalState =>
      ds.getRetrievalFlowById(message.flowId).flatMap {
        case res: SuccessfulRequestForRetrieval =>
          val response = message.statesToActor(StateId.transformationId.id) ? PassToTransformationState(res.flow, message.statesToActor)
          response.map {
            case notice: FailureValidationList =>
              NotEmptyFolderFailure(errorService.getMergedErrors(Seq(notice)))
            case _ =>
              NotEmptyFolderSuccessful(errorService.getSuccessfulResult(Constant.Successfully))
          }
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToRetrievalState =>
      ds.insertRetrievalFlow(message.flow).flatMap {
        case res: SuccessfulRequestForRetrieval =>
          message.statesToActor(StateId.transformationId.id) ?
            PassToTransformationState(res.flow, message.statesToActor)
        case notice: EmptyFile => Future.successful(notice)
        case notice: InvalidFileName => Future.successful(notice)
      }.mapTo[Notice].pipeTo(sender())
  }
}
