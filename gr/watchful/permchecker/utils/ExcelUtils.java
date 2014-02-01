/**
 * 
 * Source: http://viralpatel.net/blogs/java-read-write-excel-file-apache-poi/
 * ~bearbear12345~ 
 * 
 */

package gr.watchful.permchecker.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
    public static ArrayList<ArrayList<String>> toArray(File file, int sheetNum) throws FileNotFoundException,
            IOException {
        ArrayList<ArrayList<String>> rows = new ArrayList<ArrayList<String>>();//TODO actually return the thing

        FileInputStream input = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(input);
        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        for (Row row : sheet) {
            ArrayList<String> temp = new ArrayList<String>();
            for (Cell cell : row) {
                if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                    temp.add(cell.getStringCellValue());
                    //System.out.print(cell.getStringCellValue()+"  :  ");
                } else {
                    temp.add("");
                }
            }
            //System.out.print("\n");
            rows.add(temp);
        }
        return rows;
    }
}