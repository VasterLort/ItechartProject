package by.itechart.conversion

import by.itechart.action.{CsvPaymentFile, EmptyFile, Notice}
import by.itechart.conf.GeneralConf
import by.itechart.constant.Constant
import org.apache.poi.ss.usermodel.{Cell, CellType, Row}
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object XlsxToCsvConverter {
  def convert(fileName: String): Notice = {
    val workBook = new XSSFWorkbook(GeneralConf.configValues.resourcePath + fileName)
    val selSheet = workBook.getSheetAt(Constant.StartIndex)
    val sb = new StringBuffer()

    selSheet.forEach { row =>
      (Constant.StartIndex to row.getLastCellNum).map { i =>
        val cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
        sb.append(setCellType(cell))
      }

      sb.append(Constant.RowDelimiterOfFile)
    }

    if (sb != null) CsvPaymentFile(sb.toString, fileName)
    else EmptyFile()
  }

  private def setCellType(cell: Cell): String = {
    cell.getCellType() match {
      case CellType.STRING => cell.getStringCellValue() + Constant.ContentDelimiterOfFile
      case CellType.NUMERIC => cell.getNumericCellValue() + Constant.ContentDelimiterOfFile
      case CellType.BOOLEAN => cell.getBooleanCellValue() + Constant.ContentDelimiterOfFile
      case CellType.BLANK => Constant.ContentDelimiterOfFile
    }
  }
}
