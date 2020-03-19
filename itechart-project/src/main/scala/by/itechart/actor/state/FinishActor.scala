package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.util.Timeout
import by.itechart.action.PassToFinishState
import by.itechart.service.DatabaseService

import scala.concurrent.duration._

class FinishActor(
                   private val ds: DatabaseService = new DatabaseService
                 ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: PassToFinishState =>
      ds.insertFlow(message.flow)
  }
}
