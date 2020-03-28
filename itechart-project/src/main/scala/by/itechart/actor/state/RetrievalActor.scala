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

class RetrievalActor(
                      private val ds: DatabaseService = new DatabaseService
                    ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: RunRetrievalState =>
      ds.getFlowById(message.flowId, StateId.retrievalId.id).flatMap {
        case res: SuccessfulRequest =>
          message.statesToActor(StateId.transformationId.id) ?
            PassToTransformationState(res.flow.copy(statusId = StateId.transformationId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureRequest => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToRetrievalState =>
      ds.insertRetrievalFlow(message.flow).flatMap {
        case res: SuccessfulRequest =>
          message.statesToActor(StateId.transformationId.id) ?
            PassToTransformationState(res.flow.copy(statusId = StateId.transformationId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureRequest => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
  }
}
