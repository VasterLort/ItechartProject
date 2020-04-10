package by.itechart.dao

import by.itechart.conf.DictionaryConf
import by.itechart.database.DatabaseConfig
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

case class Dictionary(
                       key: String,
                       value: String
                     )

class DictionaryDao(
                     private val dbProvider: DatabaseConfig.type = DatabaseConfig
                   ) {
  private val db = dbProvider.db
  private val dictionary = TableQuery[DictionaryTable]

  def getKeys(): Future[Seq[Dictionary]] = {
    db.run(dictionary.filter(value =>
      value.valueName === DictionaryConf.configValues.company ||
        value.valueName === DictionaryConf.configValues.department ||
        value.valueName === DictionaryConf.configValues.payDate
    ).result)
  }

  def getDictionary(): Future[Seq[Dictionary]] = {
    db.run(dictionary.result)
  }

  private class DictionaryTable(tag: Tag) extends Table[Dictionary](tag, "dictionary") {
    def keyName = column[String]("key_name")

    def valueName = column[String]("value_name")

    def * = (keyName, valueName) <> (Dictionary.tupled, Dictionary.unapply)
  }

}