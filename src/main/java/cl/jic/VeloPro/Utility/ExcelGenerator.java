package cl.jic.VeloPro.Utility;

import cl.jic.VeloPro.Model.Entity.Kardex;
import cl.jic.VeloPro.Model.Entity.Sale.Sale;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelGenerator {

    @Autowired private NotificationManager notificationManager;

    public void createSaleFile(List<Sale> list, String sheetName) throws IOException {
        String directoryPath = "C:\\Users\\juano\\Desktop\\excels\\";

        TextInputDialog dialog = new TextInputDialog("Ventas");
        dialog.setTitle("Nombre del Archivo");
        dialog.setHeaderText("Ingrese el nombre del archivo");
        dialog.setContentText("Nombre:");
        ButtonType buttonTypeOk = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String fileName = result.get();
            String excelPath = directoryPath + fileName + ".xls";
            try (Workbook workbook = new HSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(excelPath)){
                Sheet sheet = workbook.createSheet(sheetName);

                // Crear estilo para el encabezado
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // Crear estilo para las celdas
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                Row headerRow = sheet.createRow(0);
                String[] headers = {"Documento", "Fecha", "Método de Pago", "Impuesto", "Total", "Comentario"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                int rowNum = 1;
                for (Sale sale : list) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(sale.getDocument());
                    row.createCell(1).setCellValue(sale.getDate().toString());
                    row.createCell(2).setCellValue(sale.getPaymentMethod().name());
                    row.createCell(3).setCellValue(sale.getTax());
                    row.createCell(4).setCellValue(sale.getTotalSale());
                    row.createCell(5).setCellValue(sale.getComment());
                    for (int i = 0; i < headers.length; i++) {
                        row.getCell(i).setCellStyle(cellStyle);
                    }
                }
                sheet.protectSheet("1234");
                for (int i = 0; i < headers.length; i++) {
                    sheet.setColumnWidth(i, 5000);
                }
                workbook.write(outputStream);

                Platform.runLater(() -> {
                    notificationManager.successNotification("Archivo creado", "El archivo se ha guardado correctamente en: " + excelPath, Pos.TOP_CENTER);
                });
            } catch (IOException e) {
                e.printStackTrace();
                    Platform.runLater(() -> {
                        notificationManager.errorNotification("Error", "Ha ocurrido un error al crear el archivo."+ e.getMessage(), Pos.TOP_CENTER);
                    });
            }
        }
    }

    public void createKardexFile(List<Kardex> list, String sheetName) throws IOException {
        String directoryPath = "C:\\Users\\juano\\Desktop\\excels\\";

        TextInputDialog dialog = new TextInputDialog("Kardex");
        dialog.setTitle("Nombre del Archivo");
        dialog.setHeaderText("Ingrese el nombre del archivo");
        dialog.setContentText("Nombre:");
        ButtonType buttonTypeOk = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String fileName = result.get();
            String excelPath = directoryPath + fileName + ".xls";
            try (Workbook workbook = new HSSFWorkbook(); FileOutputStream outputStream = new FileOutputStream(excelPath)){
                Sheet sheet = workbook.createSheet(sheetName);

                // Crear estilo para el encabezado
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // Crear estilo para las celdas
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setBorderBottom(BorderStyle.THIN);
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                Row headerRow = sheet.createRow(0);
                String[] headers = {"Fecha", "Descripción", "Stock", "Precio", "Movimiento", "Cantidad", "Observación"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                int rowNum = 1;
                for (Kardex kardex : list) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(kardex.getDate());
                    row.createCell(1).setCellValue(kardex.getProduct().getDescription());
                    row.createCell(2).setCellValue(kardex.getStock());
                    row.createCell(3).setCellValue(kardex.getPrice());
                    row.createCell(4).setCellValue(kardex.getMovementsType().toString());
                    row.createCell(5).setCellValue(kardex.getQuantity());
                    row.createCell(6).setCellValue(kardex.getComment());
                    for (int i = 0; i < headers.length; i++) {
                        row.getCell(i).setCellStyle(cellStyle);
                    }
                }
                sheet.protectSheet("1234");
                for (int i = 0; i < headers.length; i++) {
                    sheet.setColumnWidth(i, 5000);
                }
                workbook.write(outputStream);

                Platform.runLater(() -> {
                    notificationManager.successNotification("Archivo creado", "El archivo se ha guardado correctamente en: " + excelPath, Pos.TOP_CENTER);
                });
            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    notificationManager.errorNotification("Error", "Ha ocurrido un error al crear el archivo."+ e.getMessage(), Pos.TOP_CENTER);
                });
            }
        }
    }
}