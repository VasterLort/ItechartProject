package by.itechart.dao.initialization

import by.itechart.dao.{DictionaryDao, FlowDao, RetrievalDao, TransformationDao}

object Daos {
  lazy val flowDao: FlowDao = new FlowDao()
  lazy val retrievalDao: RetrievalDao = new RetrievalDao()
  lazy val transformationDao: TransformationDao = new TransformationDao()
  lazy val dictionaryDao: DictionaryDao = new DictionaryDao()
}
