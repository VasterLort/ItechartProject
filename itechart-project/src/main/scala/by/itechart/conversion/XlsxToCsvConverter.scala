package by.itechart.conversion

import by.itechart.action.{CsvPaymentFile, EmptyFile, Notice}
import by.itechart.conf.GeneralConf
import org.apache.poi.ss.usermodel.{Cell, CellType, Row}
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object XlsxToCsvConverter {
  def convert(fileName: String): Notice = {
    val workBook = new XSSFWorkbook(GeneralConf.configValues.resourcePath + fileName)
    val selSheet = workBook.getSheetAt(GeneralConf.configValues.startIndex)
    val sb = new StringBuffer()

    selSheet.forEach { row =>
      (GeneralConf.configValues.startIndex to row.getLastCellNum).map { i =>
        val cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)
        sb.append(setCellType(cell))
      }

      sb.append(GeneralConf.configValues.rowDelimiterOfFile)
    }

    if (sb != null) CsvPaymentFile(sb.toString, fileName)
    else EmptyFile()
  }

  private def setCellType(cell: Cell): String = {
    cell.getCellType() match {
      case CellType.STRING => cell.getStringCellValue() + GeneralConf.configValues.contentDelimiterOfFile
      case CellType.NUMERIC => cell.getNumericCellValue() + GeneralConf.configValues.contentDelimiterOfFile
      case CellType.BOOLEAN => cell.getBooleanCellValue() + GeneralConf.configValues.contentDelimiterOfFile
      case CellType.BLANK => GeneralConf.configValues.contentDelimiterOfFile
    }
  }
}
