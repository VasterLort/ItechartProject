package by.itechart

import by.itechart.state._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Application extends App {
  val result = Start.initFlow().flatMap { statusStart =>
    if (statusStart.statusId == 1) {
      Retrieve.updateFlow(statusStart).flatMap { statusRetrieve =>
        if (statusRetrieve.statusId == 2) {
          Transform.updateFlow(statusRetrieve).flatMap { statusTransform =>
            if (statusTransform.statusId == 3) {
              Normalize.updateFlow(statusTransform).flatMap { statusNormalize =>
                if (statusNormalize.statusId == 4) {
                  Validate.updateFlow(statusNormalize).flatMap { statusValidate =>
                    if (statusValidate.statusId == 5) {
                      Load.updateFlow(statusValidate).map { statusLoad =>
                        if (statusLoad.statusId == 6) {
                          Finish.updateFlow(statusLoad)
                        } else statusLoad
                      }
                    } else Future.successful(statusValidate)
                  }
                } else Future.successful(statusNormalize)
              }
            } else Future.successful(statusTransform)
          }
        } else Future.successful(statusRetrieve)
      }
    } else Future.successful(statusStart)
  }

  Await.result(result, 720.seconds)
}
