package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action.{RunNormalizationState, RunValidationState}
import by.itechart.config.AppConfig
import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao}
import by.itechart.date.MyDate
import by.itechart.enums.StateId

class Normalization(
                     val flowDao: FlowDao = Daos.flowDao,
                     val flow: Flow = Flow(AppConfig.configValues.initFlow, StateId.normalizeId.id, MyDate.getCurrentDate())
                   ) extends Actor with ActorLogging {
  def receive = {
    case message: RunNormalizationState =>
      val updatedFlow = flow.copy(flowId = message.flowId)
      log.info(s"StateNormalize: StatusID = ${updatedFlow.statusId}, FlowId = ${updatedFlow.flowId}")
      message.statesToActor(StateId.validateId.id) ! RunValidationState(message.flowId, message.statesToActor)
  }
}
