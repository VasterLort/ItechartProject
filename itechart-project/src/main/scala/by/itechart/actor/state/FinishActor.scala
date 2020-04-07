package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import by.itechart.action._
import by.itechart.constant.{Constant, StateId}
import by.itechart.service.DatabaseService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class FinishActor(
                   private val ds: DatabaseService = new DatabaseService
                 ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(Constant.TimeoutSec.seconds)

  def receive = {
    case message: RunFinishState =>
      ds.getFlowById(message.flowId, StateId.finishId.id).flatMap {
        case res: SuccessfulRequest =>
          ds.insertFlow(res.flow).mapTo[Notice].pipeTo(sender())
        case res: FailureRequest => Future.successful(res)
      }.mapTo[Notice].pipeTo(sender())
    case message: PassToFinishState =>
      ds.insertFlow(message.flow).mapTo[Notice].pipeTo(sender())
  }
}
