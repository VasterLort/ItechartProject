package by.itechart.conversion

import java.io.File

import by.itechart.action.{CsvPaymentFile, Notice}
import by.itechart.config.AppConfig
import org.apache.poi.ss.usermodel.{CellType, Row}
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object Converter {
  def convertXlsxToCsv(fileName: String): Notice = {
    val workBook = new XSSFWorkbook(AppConfig.configValues.resourcePath + fileName)
    new File(AppConfig.configValues.resourcePath + fileName).delete()
    val selSheet = workBook.getSheetAt(AppConfig.configValues.startIndex)
    val sb = new StringBuffer()

    selSheet.forEach { row =>
      for (i <- 0 to row.getLastCellNum) {
        val cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)

        cell.getCellType() match {
          case CellType.STRING => sb.append(cell.getStringCellValue() + AppConfig.configValues.contentDelimiterOfFile)
          case CellType.NUMERIC => sb.append(cell.getNumericCellValue() + AppConfig.configValues.contentDelimiterOfFile)
          case CellType.BOOLEAN => sb.append(cell.getBooleanCellValue() + AppConfig.configValues.contentDelimiterOfFile)
          case CellType.BLANK => sb.append(AppConfig.configValues.contentDelimiterOfFile)
        }
      }

      sb.append(AppConfig.configValues.rowDelimiterOfFile)
    }

    CsvPaymentFile(sb.toString, fileName)
  }
}
