package cl.jic.VeloPro.Utility;

import cl.jic.VeloPro.Model.DTO.DetailSaleDTO;
import cl.jic.VeloPro.Model.Entity.Sale.Sale;
import cl.jic.VeloPro.Model.Enum.PaymentMethod;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;
import org.apache.pdfbox.util.Matrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PDFGenerator {
    @Autowired NotificationManager notificationManager;
    private static final String phone = "+569 12345678";
    private static final String address = "Calle Falsa 1234";
    private static final String email = "soporte@gmail.com";

    public static void generateSaleReceiptPDF(Sale sale, List<DetailSaleDTO> dtoList) {
        String userHome = System.getProperty("user.home");
        String directoryPath = userHome + File.separator + "Documents" + File.separator + "Boletas" + File.separator;
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
        }
        String fileName = sale.getDocument() + ".pdf";
        String filePath = directoryPath + fileName;

        try (PDDocument document = PDDocument.load(new File(  "C:\\Users\\juano\\Desktop\\VeloPro\\src\\main\\resources\\PDFTemplate\\PlantillaBoleta.pdf"))) {
            // Obtener el formulario interactivo (AcroForm) de la plantilla
            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();

            if (acroForm != null) {
                PDField phoneField = acroForm.getField("Text12");
                phoneField.setValue(phone);
                PDField emailField = acroForm.getField("Text13");
                emailField.setValue(email);
                PDField addressField = acroForm.getField("Text14");
                addressField.setValue(address);

                PDField docField = acroForm.getField("Text7");
                docField.setValue(sale.getDocument());
                if (docField instanceof PDVariableText) {
                    ((PDVariableText) docField).setDefaultAppearance("/Helv 24 Tf 0 g");
                }

                PDField dateField = acroForm.getField("Text2");
                dateField.setValue(sale.getDate().toString());

                PDField paymentMethodField = acroForm.getField("Text3");
                paymentMethodField.setValue(sale.getPaymentMethod().toString());

                PDField totalSaleField = acroForm.getField("Text4");
                totalSaleField.setValue("$" + sale.getTotalSale());

                PDField taxField = acroForm.getField("Text5");
                taxField.setValue("$" + sale.getTax());

                if (sale.getDiscount() > 0) {
                    PDField discountField = acroForm.getField("Text15");
                    discountField.setValue("$" + sale.getDiscount());
                }

                if (sale.getPaymentMethod().equals(PaymentMethod.PRESTAMO)) {
                    PDField debtField = acroForm.getField("Text6");
                    debtField.setValue("$" + sale.getTotalSale());

                    PDField customerField = acroForm.getField("Text1");
                    customerField.setValue(sale.getCostumer().getName());
                }

                PDField totalSaleTable = acroForm.getField("Text11");
                totalSaleTable.setValue("$" + sale.getTotalSale());

                int rowIndex = 0;
                int maxRows = 12;

                for (DetailSaleDTO dto : dtoList) {
                    if (rowIndex >= maxRows) break;
                    String quantityFieldName = "Text" + (16 + rowIndex * 3);
                    String descriptionFieldName = "Text" + (17 + rowIndex * 3);
                    String priceFieldName = "Text" + (18 + rowIndex * 3);

                    PDField quantityField = acroForm.getField(quantityFieldName);
                    PDField descriptionField = acroForm.getField(descriptionFieldName);
                    PDField priceField = acroForm.getField(priceFieldName);

                    if (quantityField != null) {
                        quantityField.setValue(String.valueOf(dto.getQuantity()));
                    }
                    if (descriptionField != null) {
                        descriptionField.setValue(dto.getDescription());
                    }
                    if (priceField != null) {
                        priceField.setValue("$" + dto.getTotal());
                    }
                    rowIndex++;
                }
            }
            document.save(filePath);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se ha podido crear el archivo");
        }
    }

    public void openPDF(String pdfPath) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar impresión");
        alert.setHeaderText("¿Desea imprimir el PDF?");
        alert.setContentText("Se abrirá el PDF en su aplicación de impresión predeterminada.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            File pdfFile = new File(pdfPath);
            if (pdfFile.exists() && pdfFile.length() > 0) {
                try {
                    String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";

                    ProcessBuilder pb = new ProcessBuilder(chromePath, pdfFile.getAbsolutePath());
                    pb.start();

                } catch (IOException e) {
                    notificationManager.errorNotification("Error!", "No se ha podido abrir el archivo", Pos.TOP_CENTER);
                }
            }
        }
    }

    public void addWatermarkToPDF(String pdfFilePath) {
        File file = new File(pdfFilePath);

        try (PDDocument document = PDDocument.load(file)) {
            PDPage page = document.getPage(0);
            addWatermark(document, page);
            document.save(pdfFilePath);
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo agregar marca de agua a la boleta");
        }
    }

    private static void addWatermark(PDDocument document, PDPage page) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);

        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 60);
        contentStream.setNonStrokingColor(Color.LIGHT_GRAY);

        String watermarkText = "Anulado";
        float angle = 90;
        float x = page.getMediaBox().getWidth() / 2;
        float y = page.getMediaBox().getHeight() / 2;

        contentStream.saveGraphicsState();
        contentStream.transform(Matrix.getRotateInstance(Math.toRadians(angle), x, y));
        contentStream.beginText();
        contentStream.newLineAtOffset(-30, -30);
        contentStream.showText(watermarkText);
        contentStream.endText();
        contentStream.restoreGraphicsState();

        contentStream.close();
    }
}
