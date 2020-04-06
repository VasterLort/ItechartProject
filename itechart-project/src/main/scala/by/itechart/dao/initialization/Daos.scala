package by.itechart.dao.initialization

import by.itechart.dao._

object Daos {
  lazy val flowDao: FlowDao = new FlowDao()
  lazy val retrievalDao: RetrievalDao = new RetrievalDao()
  lazy val transformationDao: TransformationDao = new TransformationDao()
  lazy val normalizationDao: NormalizationDao = new NormalizationDao()
  lazy val dictionaryDao: DictionaryDao = new DictionaryDao()
}
