package by.itechart.conversion

import by.itechart.action.{CsvPaymentFile, Notice, XlsxPaymentFile}
import by.itechart.config.AppConfig
import org.apache.poi.ss.usermodel.{CellType, Row}
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object Converter {
  def convertXlsxToCsv(fileName: String): Notice = {
    val workBook = new XSSFWorkbook(AppConfig.configValues.resourcePath + fileName);
    val selSheet = workBook.getSheetAt(0);
    val sb = new StringBuffer();

    selSheet.forEach { row =>
      for (i <- 0 to row.getLastCellNum) {
        val cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK)

        cell.getCellType() match {
          case CellType.STRING => sb.append(cell.getStringCellValue() + ",")
          case CellType.NUMERIC => sb.append(cell.getNumericCellValue() + ",")
          case CellType.BOOLEAN => sb.append(cell.getBooleanCellValue() + ",")
          case CellType.BLANK => sb.append(" ,")
        }
      }
      sb.deleteCharAt(sb.length() - 1)
      sb.deleteCharAt(sb.length() - 2)
      sb.append("\r\n")
    }

    CsvPaymentFile(sb.toString, fileName)
  }
}
