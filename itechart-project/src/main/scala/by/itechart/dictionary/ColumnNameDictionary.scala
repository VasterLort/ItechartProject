package by.itechart.dictionary

case class ColumnNameDictionary(
                                 columnName: Map[String, String]
                               )

object ColumnNameDictionary {
  lazy val values = ColumnNameDictionary(
    Map(
      "COMPANY_NAME" -> "COMPANY_NAME",
      "Company name" -> "COMPANY_NAME",
      "DEPARTMENT" -> "DEPARTMENT",
      "DEPARTMENT_NAME" -> "DEPARTMENT",
      "Department name" -> "DEPARTMENT",
      "PAY_DATE" -> "PAY_DATE",
      "pay date" -> "PAY_DATE"
    ))
}
