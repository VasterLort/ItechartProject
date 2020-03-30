package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.conf.GeneralConf
import by.itechart.enums.StateId
import by.itechart.service.InitializationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class InitializationActor(
                           private val service: InitializationService = new InitializationService()
                         ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(10.seconds)

  def receive = {
    case message: RunInitializationState =>
      service.getPaymentFilenames() match {
        case _: EmptyFolder => EmptyFolder()
        case res: PaymentFileName =>
          (GeneralConf.configValues.startIndex until res.name.size()).map { index =>
            val fileName = res.name.get(index).toString
            (message.statesToActor(StateId.startId.id) ?
              PassToStartState(fileName.substring(fileName.lastIndexOf(' ') + GeneralConf.configValues.fileNameIndex), message.statesToActor)).mapTo[Notice].pipeTo(sender())
          }
      }
  }
}
