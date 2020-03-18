package by.itechart.actor.state

import akka.actor.{Actor, ActorLogging}
import by.itechart.action._
import by.itechart.config.AppConfig
import by.itechart.dao.initialization.Daos
import by.itechart.dao.{Flow, FlowDao}
import by.itechart.date.MyDate
import by.itechart.enums.StateId

class Retrieval(
                 val flowDao: FlowDao = Daos.flowDao,
                 val flow: Flow = Flow(AppConfig.configValues.initFlow, StateId.retrieveId.id, MyDate.getCurrentDate())
               ) extends Actor with ActorLogging {
  def receive = {
    case message: RunRetrievalState =>
      val updatedFlow = flow.copy(flowId = message.flowId)
      log.info(s"StateRetrieve: StatusID = ${updatedFlow.statusId}, FlowId = ${updatedFlow.flowId}")
      message.statesToActor(StateId.transformId.id) ! RunTransformationState(message.flowId, message.statesToActor)
  }
}
