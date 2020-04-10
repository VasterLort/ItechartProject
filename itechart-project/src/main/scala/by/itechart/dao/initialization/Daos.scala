package by.itechart.dao.initialization

import by.itechart.dao.{ValidationDao, _}

object Daos {
  lazy val flowDao: FlowDao = new FlowDao()
  lazy val retrievalDao: RetrievalDao = new RetrievalDao()
  lazy val transformationDao: TransformationDao = new TransformationDao()
  lazy val normalizationDao: NormalizationDao = new NormalizationDao()
  lazy val validationDao: ValidationDao = new ValidationDao()
  lazy val dictionaryDao: DictionaryDao = new DictionaryDao()
}
