package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout
import by.itechart.action._
import by.itechart.dao.Flow
import by.itechart.date.MyDate
import by.itechart.enums.StateId

import scala.concurrent.duration._

class StartActor extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: RunStartState =>
      val flow: Flow = Flow(message.flowId, StateId.startId.id, MyDate.getCurrentDate())
      (message.statesToActor(StateId.retrieveId.id) ?
        PassToRetrieveState(flow.copy(statusId = StateId.retrieveId.id, statusDate = MyDate.getCurrentDate()), message.statesToActor)).mapTo[Notice]
  }
}
