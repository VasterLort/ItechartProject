package by.itechart.dao.initialization

import by.itechart.dao.FlowDao

object Daos {
  lazy val flowDao: FlowDao = new FlowDao()
}
