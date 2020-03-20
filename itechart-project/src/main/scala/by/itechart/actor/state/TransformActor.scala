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

class TransformActor(
                      private val ds: DatabaseService = new DatabaseService
                    ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: RunTransformState =>
      ds.getFlowById(message.flowId, StateId.transformId.id).map {
        case res: SuccessfulNotice =>
          (message.statesToActor(StateId.normalizeId.id) ?
            PassToNormalizeState(res.flow.copy(statusId = StateId.normalizeId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor))
            .mapTo[Notice]
        case res: FailureNotice => res
      }
    case message: PassToTransformState =>
      ds.insertFlow(message.flow).map {
        case res: SuccessfulNotice =>
          (message.statesToActor(StateId.normalizeId.id) ?
            PassToNormalizeState(res.flow.copy(statusId = StateId.normalizeId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor))
            .mapTo[Notice]
        case res: FailureNotice => res
      }
  }
}
