package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.constant.{Constant, StateId}
import by.itechart.service.InitializationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class InitializationActor(
                           private val service: InitializationService = new InitializationService()
                         ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(Constant.TimeoutSec.seconds)

  def receive = {
    case message: RunInitializationState =>
      service.getPaymentFilenames() match {
        case _: EmptyFolder => Future.successful(EmptyFolder()).mapTo[Notice].pipeTo(sender())
        case res: PaymentFileName => {
          val response = (Constant.StartIndex until res.name.size()).map { index =>
            val fileName = res.name.get(index).toString
            (message.statesToActor(StateId.startId.id) ?
              PassToStartState(fileName.substring(fileName.lastIndexOf(Constant.UselessInfo) +
                Constant.FileNameIndex), message.statesToActor)).mapTo[Notice]
          }

          Future.sequence(response).mapTo[Seq[Notice]].map(seq => NotEmptyFolder(seq)).mapTo[Notice].pipeTo(sender())
        }
      }
  }
}
