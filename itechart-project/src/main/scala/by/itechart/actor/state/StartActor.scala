package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.dao.Flow
import by.itechart.date.MyDate
import by.itechart.enums.StateId
import by.itechart.service.DatabaseService
import io.jvm.uuid._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class StartActor(
                  private val ds: DatabaseService = new DatabaseService,
                ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: PassToStartState =>
      ds.insertFlow(Flow(UUID.random.toString, StateId.startId.id, MyDate.getCurrentDate())).flatMap {
        case res: SuccessfulRequest =>
          message.statesToActor(StateId.retrievalId.id) ?
            PassToRetrievalState(res.flow.copy(statusId = StateId.retrievalId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureRequest => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
    case message: RunStartState =>
      ds.getFlowById(message.flowId, StateId.startId.id).flatMap {
        case res: SuccessfulRequest =>
          message.statesToActor(StateId.retrievalId.id) ?
            PassToRetrievalState(res.flow.copy(statusId = StateId.retrievalId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)
        case res: FailureRequest => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
  }
}
