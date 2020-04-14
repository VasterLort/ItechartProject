package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import by.itechart.action._
import by.itechart.constant.{Constant, StateId}
import by.itechart.service.{ErrorService, InitializationService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class InitializationActor(
                           private val initializationService: InitializationService = new InitializationService(),
                           private val errorService: ErrorService = new ErrorService()
                         ) extends Actor with ActorLogging {
  implicit val timeout = Timeout(Constant.TimeoutSec.seconds)

  def receive = {
    case message: RunInitializationState =>
      initializationService.getPaymentFilenames() match {
        case _: EmptyFolder => Future.successful(EmptyFolder(errorService.getSuccessfulResult(Constant.EmptyFolder))).mapTo[Notice].pipeTo(sender())
        case res: PaymentFileName => {
          val response = (Constant.StartIndex until res.name.size()).map { index =>
            val fileName = res.name.get(index).toString
            (message.statesToActor(StateId.startId.id) ?
              PassToStartState(fileName.substring(fileName.lastIndexOf(Constant.UselessInfo) +
                Constant.FileNameIndex), message.statesToActor)).mapTo[Notice]
          }

          Future.sequence(response).mapTo[Seq[Notice]].map {
            case errors
              if errors.filter(_.isInstanceOf[FailureValidationList]).isEmpty &&
                errors.filter(_.isInstanceOf[EmptyFile]).isEmpty &&
                errors.filter(_.isInstanceOf[InvalidFileName]).isEmpty =>
              NotEmptyFolderSuccessful(errorService.getSuccessfulResult(Constant.Successfully))
            case errors =>
              NotEmptyFolderFailure(errorService.getMergedErrors(errors))
          }.mapTo[Notice].pipeTo(sender())
        }
      }
  }
}
