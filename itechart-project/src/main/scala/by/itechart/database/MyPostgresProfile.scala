package by.itechart.database

import com.github.tminglei.slickpg._
import org.json4s.{JValue, JsonMethods}
import slick.jdbc.JdbcType

trait MyPostgresProfile extends ExPostgresProfile
  with PgJson4sSupport {
  def pgjson = "jsonb" // jsonb support is in postgres 9.4.0 onward; for 9.3.x use "json"

  override val api = MyAPI

  object MyAPI extends API
    with Json4sJsonImplicits {

    override implicit val json4sJsonTypeMapper: JdbcType[JValue] =
      new GenericJdbcType[JValue](
        pgjson,
        (s) => org.json4s.native.JsonMethods.parse(s),
        (v) => org.json4s.native.JsonMethods.compact(org.json4s.native.JsonMethods.render(v)),
        hasLiteralForm = false
      )
  }

}

object MyPostgresProfile extends MyPostgresProfile {
  type DOCType = org.json4s.native.Document
  override val jsonMethods = org.json4s.native.JsonMethods.asInstanceOf[JsonMethods[DOCType]]
}
