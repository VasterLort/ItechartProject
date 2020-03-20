package by.itechart.enums

object StateId extends Enumeration {
  val startId = Value(1)
  val retrievalId = Value(2)
  val transformationId = Value(3)
  val normalizationId = Value(4)
  val validationId = Value(5)
  val loadId = Value(6)
  val finishId = Value(7)
}

