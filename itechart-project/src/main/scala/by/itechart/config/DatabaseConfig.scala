package by.itechart.config

import slick.jdbc.PostgresProfile

object DatabaseConfig {

  import PostgresProfile.api._

  val db = Database.forConfig("database")
}
