package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.dao.Flow
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DatabaseService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class TransformationActor(
                           private val ds: DatabaseService = new DatabaseService
                         ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(120.seconds)

  def receive = {
    case message: RunTransformationState =>
      ds.getTransformationFlowById(message.flowId).flatMap {
        case res: SuccessfulRequestForTransformation =>
          message.statesToActor(StateId.normalizationId.id) ?
            PassToNormalizationState(
              Flow(res.flow.flowId, res.flow.fileName, statusId = StateId.normalizationId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureRequest => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToTransformationState =>
      ds.insertTransformationFlow(message.flow).flatMap {
        case res: SuccessfulRequestForTransformation =>
          message.statesToActor(StateId.normalizationId.id) ?
            PassToNormalizationState(
              Flow(res.flow.flowId, res.flow.fileName, statusId = StateId.normalizationId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureTransformation => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
  }
}
