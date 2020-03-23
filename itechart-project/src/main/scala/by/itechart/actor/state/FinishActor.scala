package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.pipe
import akka.util.Timeout
import by.itechart.action.{Notice, PassToFinishState}
import by.itechart.service.DatabaseService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class FinishActor(
                   private val ds: DatabaseService = new DatabaseService
                 ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: PassToFinishState =>
      ds.insertFlow(message.flow).mapTo[Notice].pipeTo(sender())
  }
}
