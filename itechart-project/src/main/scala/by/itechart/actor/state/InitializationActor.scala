package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.conf.GeneralConf
import by.itechart.enums.StateId
import by.itechart.service.InitializationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class InitializationActor(
                           private val service: InitializationService = new InitializationService()
                         ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(120.seconds)

  def receive = {
    case message: RunInitializationState =>
      service.getPaymentFilenames() match {
        case _: EmptyFolder => EmptyFolder()
        case res: PaymentFileName =>
          val response = (GeneralConf.configValues.startIndex until res.name.size()).map { index =>
            val fileName = res.name.get(index).toString
            (message.statesToActor(StateId.startId.id) ?
              PassToStartState(fileName.substring(fileName.lastIndexOf(' ') +
                GeneralConf.configValues.fileNameIndex), message.statesToActor)).mapTo[Notice]
          }

          log.info(sender().path.toString + " 12345")
          Future.sequence(response).mapTo[Seq[Notice]].pipeTo(sender())
      }
  }
}
