package by.itechart.dictionary

import by.itechart.action.{CorrectDictionary, IncorrectDictionary, Notice, PreparedDictionary}
import by.itechart.service.DictionaryService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Dictionary(
                  private val dictionaryService: DictionaryService = new DictionaryService()
                ) {
  def getFileNameKeys(): Future[Notice] = {
    dictionaryService.getKeys().map {
      case res: CorrectDictionary => PreparedDictionary(res.dictionary.map(seq => seq.key -> seq.value).toMap)
      case _ => IncorrectDictionary()
    }
  }

  def getDictionary(): Future[Notice] = {
    dictionaryService.getDictionary().map {
      case res: CorrectDictionary => PreparedDictionary(res.dictionary.map(seq => seq.key -> seq.value).toMap)
      case _ => IncorrectDictionary()
    }
  }
}
