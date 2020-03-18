package by.itechart.date

import java.util.Calendar

object MyDate {
  def getCurrentDate(): String = {
    Calendar.getInstance().getTime().toString
  }
}
