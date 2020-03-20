package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.dao.Flow
import by.itechart.date.MyDate
import by.itechart.enums.StateId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class StartActor extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: RunStartState =>
      val flow: Flow = Flow(message.flowId, StateId.startId.id, MyDate.getCurrentDate())
      (message.statesToActor(StateId.retrievalId.id) ?
        PassToRetrievalState(flow.copy(statusId = StateId.retrievalId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor))
        .mapTo[Notice].pipeTo(sender())
  }
}
