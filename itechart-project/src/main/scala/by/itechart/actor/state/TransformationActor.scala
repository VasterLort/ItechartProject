package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.constant.{Constant, StateId}
import by.itechart.service.DatabaseService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class TransformationActor(
                           private val ds: DatabaseService = new DatabaseService
                         ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(Constant.TimeoutSec.seconds)

  def receive = {
    case message: RunTransformationState =>
      ds.getTransformationFlowById(message.flowId).flatMap {
        case res: SuccessfulRequestForTransformation =>
          message.statesToActor(StateId.normalizationId.id) ?
            PassToNormalizationState(res.flow.toList, message.statesToActor)
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
    case message: RunTransformationStateByKeys =>
      ds.getTransformationFlowByKeys(message.flowId, message.companyName, message.departmentName, message.payDate).flatMap {
        case res: SuccessfulRequestForTransformation =>
          message.statesToActor(StateId.normalizationId.id) ?
            PassToNormalizationState(res.flow.toList, message.statesToActor)
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToTransformationState =>
      ds.insertTransformationFlow(message.flow).flatMap {
        case res: SuccessfulRequestForTransformation =>
          message.statesToActor(StateId.normalizationId.id) ?
            PassToNormalizationState(res.flow.toList, message.statesToActor)
        case _ => Future.successful(FailureRequest())
      }.mapTo[Notice].pipeTo(sender())
  }
}
