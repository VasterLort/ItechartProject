package by.itechart.dao.initialization

import by.itechart.dao.{FlowDao, RetrievalDao}

object Daos {
  lazy val flowDao: FlowDao = new FlowDao()
  lazy val retrievalDao: RetrievalDao = new RetrievalDao()
}
