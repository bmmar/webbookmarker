package com.brian.webbookmarker.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelData {
    private ObservableList<ExcelDataItem> allData;
    private ObservableSet<String> myCategories;
    private static final ExcelData instance = new ExcelData();
    private static int finalEntryID = 0;

    public static ExcelData getInstance() {
        return instance;
    }

    public static void incrementFinalEntryID() {
        finalEntryID++;
    }

    public ObservableList<ExcelDataItem> getAllData() {
        return allData;
    }

    public ExcelDataItem getItemFromTitle (String title) {
        ObservableList<ExcelDataItem> allItems = getAllData();
        for (ExcelDataItem item : allItems) {
            if (item.getTitle().equals(title)) {
                return item;
            }
        }
        return null;
    }

    public void addItem(ExcelDataItem item) {
        allData.add(item);
    }

    public ObservableSet<String> getCategories() {
        return myCategories;
    }

    public void readExcelFileWithHeaderFirstRow(String fileName, boolean allCellHaveData) {
        // data is read as an array list of ExcelDataItems - i.e. a row in the table
        int headerCount = 0;
        allData = FXCollections.observableArrayList(ExcelDataItem.extractor);
        myCategories = FXCollections.observableSet();

        try (FileInputStream fis = new FileInputStream(fileName);
             Workbook wb = WorkbookFactory.create(fis)) {  // looks like creating a workbook in this context means creating an object
            // to call methods (such as getSheetAt()) on.
            // filename is passed as an argument, try with resources taking two resource statements?
            // See https://www.tutorialspoint.com/how-to-declare-multiple-resources-in-a-try-with-resources-statement-in-java-9
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                String value;
                List<String> rowData = new ArrayList<>();
                if (i == 0) {
                    headerCount = row.getLastCellNum();
                }

                for (int c = 0; c < row.getLastCellNum(); c++) {
                    Cell cell = row.getCell(c);
                    if (cell != null) {

                        switch (cell.getCellType()) {
                            case FORMULA:
                                value = "FORMULA value=" + cell.getCellFormula();
                                rowData.add(cell.getCellFormula());
                                break;

                            case NUMERIC:
                                value = "NUMERIC value=" + cell.getNumericCellValue();
                                rowData.add(Double.toString(cell.getNumericCellValue()));
                                break;

                            case STRING:
                                value = "STRING value=" + cell.getStringCellValue();
                                rowData.add(cell.getStringCellValue());
                                break;

                            case BLANK:
                                value = "<BLANK>";
                                rowData.add("");
                                break;

                            case BOOLEAN:
                                value = "BOOLEAN value-" + cell.getBooleanCellValue();
                                rowData.add(cell.getBooleanCellValue() ? "true" : "false");
                                break;

                            case ERROR:
                                value = "ERROR value=" + cell.getErrorCellValue();
                                rowData.add(Byte.toString(cell.getErrorCellValue()));
                                break;

                            default:
                                value = "UNKNOWN value of type " + cell.getCellType();
                                rowData.add(cell.getCellType().toString());
                        }
                    } else {
                        rowData.add("<Blank>");
                    }
                }

                if (!allCellHaveData) {
                    int currentRowCount = row.getLastCellNum();
                    while (currentRowCount < headerCount) {
                        rowData.add("<Blank end column>");
                        currentRowCount++;
                    }
                }
                ExcelDataItem item = new ExcelDataItem(rowData);
                allData.add(item);
                // System.out.println(item);
                myCategories.add(rowData.get(rowData.size() - 1));
                int tempId = item.getId();
                if (tempId > finalEntryID) {
                    finalEntryID = tempId;
                }
                // System.out.println(finalEntryID);

            }
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void writeExcelFileWithHeaderFirstRow() {

        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("BookmarksTestData");
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("ID");
        headerCell.setCellStyle(style);
        headerCell = headerRow.createCell(1);
        headerCell.setCellValue("Title");
        headerCell.setCellStyle(style);
        headerCell = headerRow.createCell(2);
        headerCell.setCellValue("URL");
        headerCell.setCellStyle(style);
        headerCell = headerRow.createCell(3);
        headerCell.setCellValue("Description");
        headerCell.setCellStyle(style);
        headerCell = headerRow.createCell(4);
        headerCell.setCellValue("Category");
        headerCell.setCellStyle(style);

        int rowNum = 1;
        for (ExcelDataItem element : allData) {
            Row row = sheet.createRow(rowNum);
            int cellnum = 0;
            Cell cell = row.createCell(cellnum);
            cell.setCellValue(element.getIdAsString());
            sheet.autoSizeColumn(cellnum);
            cellnum++;
            cell = row.createCell(cellnum);
            cell.setCellValue(element.getTitle());
            sheet.autoSizeColumn(cellnum);
            cellnum++;
            cell = row.createCell(cellnum);
            cell.setCellValue(element.getUrlAddress());
            sheet.autoSizeColumn(cellnum);
            cellnum++;
            cell = row.createCell(cellnum);
            cell.setCellValue(element.getDescription());
            sheet.autoSizeColumn(cellnum);
            cellnum++;
            cell = row.createCell(cellnum);
            cell.setCellValue(element.getCategory());
            sheet.autoSizeColumn(cellnum);
            rowNum++;
        }

        try {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream("testRead.xlsx");
            workbook.write(out);
            out.close();
            // System.out.println("testRead.xlsx written successfully on disk.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getFinalEntryID() {
        return finalEntryID;
    }
}