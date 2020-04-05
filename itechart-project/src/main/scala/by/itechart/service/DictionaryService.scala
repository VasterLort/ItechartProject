package by.itechart.service

import by.itechart.action.{CorrectDictionary, IncorrectDictionary, Notice}
import by.itechart.dao.DictionaryDao
import by.itechart.dao.initialization.Daos

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DictionaryService(
                         private val dictionaryDao: DictionaryDao = Daos.dictionaryDao
                       ) {
  def getKeys(): Future[Notice] = {
    dictionaryDao.getKeys().map {
      case dictionary if dictionary.nonEmpty => CorrectDictionary(dictionary.toList)
      case _ => IncorrectDictionary()
    }
  }

  def getDictionary(): Future[Notice] = {
    dictionaryDao.getDictionary().map {
      case dictionary if dictionary.nonEmpty => CorrectDictionary(dictionary.toList)
      case _ => IncorrectDictionary()
    }
  }
}
