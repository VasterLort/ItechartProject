package by.itechart.date

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, YearMonth}
import java.util.Calendar

import by.itechart.action.{CorrectDate, IncorrectDate, Notice}
import by.itechart.constant.Constant

object MyDate {
  private val NullDayMonth = "0"
  private val TheLastMonthNumber = 12
  private val TheEarliestDate = "1920-12-31"
  private val TheLatestDate = "2000-01-01"
  private val Format1 = "\\d{2}/\\d{2}/\\d{4}"
  private val Format2 = "\\d{8}"
  private val Format3 = "\\d{4}-\\d{2}-\\d{2}"
  private val Format4 = "\\d{2}.\\d{2}.\\d{4}"

  private val DayEndIndexFormat1 = 2
  private val MonthStartIndexFormat1 = 3
  private val MonthEndIndexFormat1 = 5
  private val YearStartIndexFormat1 = 6

  private val DayEndIndexFormat2 = 2
  private val MonthStartIndexFormat2 = 2
  private val MonthEndIndexFormat2 = 4
  private val YearStartIndexFormat2 = 4

  private val DayEndIndexFormat3 = 4
  private val MonthStartIndexFormat3 = 5
  private val MonthEndIndexFormat3 = 7
  private val YearStartIndexFormat3 = 8

  private val DayEndIndexFormat4 = 2
  private val MonthStartIndexFormat4 = 3
  private val MonthEndIndexFormat4 = 5
  private val YearStartIndexFormat4 = 6

  private lazy val formatter1 = DateTimeFormatter.ofPattern("ddMMyyyy")
  private lazy val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def getCurrentDate(): String = {
    Calendar.getInstance().getTime().toString
  }

  def convert(value: String): Notice = {
    prepareDateValue(value) match {
      case v: CorrectDate => CorrectDate(LocalDate.parse(v.date, formatter1).toString)
      case _ => IncorrectDate()
    }
  }

  def getConvertedDate(value: String): LocalDate = {
    LocalDate.parse(value, formatter2)
  }

  def getTheEarliestDate: LocalDate = {
    LocalDate.parse(TheEarliestDate, formatter2)
  }

  def getTheLatestDate: LocalDate = {
    LocalDate.parse(TheLatestDate, formatter2)
  }

  private def prepareDateValue(value: String): Notice = {
    value match {
      case v if v.matches(Format1) =>
        checkDateValue(
          v.substring(Constant.StartIndex, DayEndIndexFormat1),
          v.substring(MonthStartIndexFormat1, MonthEndIndexFormat1),
          v.substring(YearStartIndexFormat1)
        )
      case v if v.matches(Format2) =>
        checkDateValue(
          v.substring(Constant.StartIndex, DayEndIndexFormat2),
          v.substring(MonthStartIndexFormat2, MonthEndIndexFormat2),
          v.substring(YearStartIndexFormat2)
        )
      case v if v.matches(Format3) =>
        checkDateValue(
          v.substring(YearStartIndexFormat3),
          v.substring(MonthStartIndexFormat3, MonthEndIndexFormat3),
          v.substring(Constant.StartIndex, DayEndIndexFormat3)
        )
      case v if v.matches(Format4) =>
        checkDateValue(
          v.substring(Constant.StartIndex, DayEndIndexFormat4),
          v.substring(MonthStartIndexFormat4, MonthEndIndexFormat4),
          v.substring(YearStartIndexFormat4)
        )
      case _ => IncorrectDate()
    }
  }

  private def checkDateValue(day: String, month: String, year: String): Notice = {
    val m = if (month.startsWith(NullDayMonth)) month.substring(Constant.StartIndex).toInt else month.toInt
    val d = if (day.startsWith(NullDayMonth)) day.substring(Constant.StartIndex).toInt else day.toInt

    m match {
      case _ if m <= TheLastMonthNumber && m > Constant.StartIndex =>
        val yearMonthObject = YearMonth.of(year.toInt, m);
        val daysInMonth = yearMonthObject.lengthOfMonth();
        if (d < daysInMonth && d > Constant.StartIndex) CorrectDate(day + month + year) else IncorrectDate()
      case _ => IncorrectDate()
    }
  }
}
