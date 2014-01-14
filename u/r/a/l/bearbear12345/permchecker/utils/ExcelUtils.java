/**
 * 
 * Source: http://viralpatel.net/blogs/java-read-write-excel-file-apache-poi/
 * ~bearbear12345~ 
 * 
 */
package u.r.a.l.bearbear12345.permchecker.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
	public static void readXSSF(File input) {
		try {

			FileInputStream file = new FileInputStream(input);

			// Get the workbook instance for XLS file
			XSSFWorkbook workbook = new XSSFWorkbook(file);

			// Get first sheet from the workbook
			XSSFSheet sheet = workbook.getSheetAt(1);

			// Iterate through each rows from first sheet
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				// For each row, iterate through each columns
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {

					Cell cell = cellIterator.next();

					switch (cell.getCellType()) {
					case Cell.CELL_TYPE_BOOLEAN:
						System.out.print(cell.getBooleanCellValue() + "\t\t");
						break;
					case Cell.CELL_TYPE_NUMERIC:
						System.out.print(cell.getNumericCellValue() + "\t\t");
						break;
					case Cell.CELL_TYPE_STRING:
						System.out.print(cell.getStringCellValue() + "\t\t");
						break;
					}
				}
				System.out.println("");
			}
			file.close();
			// FileOutputStream out =
			// new FileOutputStream(new File("C:\\test.xls"));
			// workbook.write(out);
			// out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void toArray(File input) {
		ArrayList<XSSFCell> data = new ArrayList<XSSFCell>();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(input);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		XSSFWorkbook workbook;
		Iterator<Row> rows = null;
		try {
			workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);
			rows = sheet.rowIterator();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (rows.hasNext()) {
			XSSFRow row1 = (XSSFRow) rows.next();
			Iterator<Cell> cells = row1.cellIterator();
			while (cells.hasNext()) {
				XSSFCell cell1 = (XSSFCell) cells.next();
				data.add(cell1);
			}
		}
		System.out.println(data);

	}

}