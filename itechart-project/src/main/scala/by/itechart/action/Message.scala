package by.itechart.action

sealed trait Message

case class StateStart(flowId: String) extends Message

case class StateRetrieve(flowId: String) extends Message

case class StateTransform(flowId: String) extends Message

case class StateNormalize(flowId: String) extends Message

case class StateValidate(flowId: String) extends Message

case class StateLoad(flowId: String) extends Message

case class StateFinish(flowId: String) extends Message

case class SuccessfulMessage(val message: String = "Request was completed!!!") extends Message

case class FailureMessage(val message: String = "Error!!!") extends Message