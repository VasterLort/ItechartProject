package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action.{RunLoadingState, RunValidationState}
import by.itechart.config.AppConfig
import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao}
import by.itechart.date.MyDate
import by.itechart.enums.StateId

class Validation(
                  val flowDao: FlowDao = Daos.flowDao,
                  val flow: Flow = Flow(AppConfig.configValues.initFlow, StateId.validateId.id, MyDate.getCurrentDate())
                ) extends Actor with ActorLogging {
  def receive = {
    case message: RunValidationState =>
      val updatedFlow = flow.copy(flowId = message.flowId)
      log.info(s"StateValidate: StatusID = ${updatedFlow.statusId}, FlowId = ${updatedFlow.flowId}")
      message.statesToActor(StateId.loadId.id) ! RunLoadingState(message.flowId, message.statesToActor)
  }
}
