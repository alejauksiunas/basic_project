package com.pdf;

import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

@Service
public class PdfService {

    public byte[] generateDocumentPdf(GeneratePdfRequest generatePdfRequest) throws IOException {
        PDDocument document = new PDDocument();
        PDRectangle pageSize = new PDRectangle(800, 1000);
        PDPage firstPage = new PDPage(pageSize);
        document.addPage(firstPage);
        PDPageContentStream pageContentStream = new PDPageContentStream(document, firstPage);
        addContentToPage(generatePdfRequest, pageContentStream, document);
        pageContentStream.close();
        ByteArrayOutputStream byteDocument = new ByteArrayOutputStream();
        document.save(byteDocument);
        document.close();
        return byteDocument.toByteArray();
    }

    private void addDocInfo(PDDocument document) {
        PDDocumentInformation docInfo = document.getDocumentInformation();
        docInfo.setAuthor("Author");
        docInfo.setTitle("Title");
        docInfo.setCreator("Creator");
        docInfo.setSubject("Subject");
        docInfo.setCreationDate(Calendar.getInstance());
        docInfo.setKeywords("PDFBox, Apache PDF testing");
    }

    //TODO: implement password protected doc
    private void protectDocument(PDDocument document) throws IOException {
        final int keyLength = 128;
        AccessPermission accessPermission = new AccessPermission();
        StandardProtectionPolicy standardProtectionPolicy = new StandardProtectionPolicy("1234", "1234", accessPermission);
        standardProtectionPolicy.setEncryptionKeyLength(keyLength);
        standardProtectionPolicy.setPermissions(accessPermission);
        document.protect(standardProtectionPolicy);
    }

    private void addContentToPage(GeneratePdfRequest generatePdfRequest, PDPageContentStream contentStream, PDDocument document) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("/img/monsterCookie.png");
        assert inputStream != null;
        PDImageXObject backgroundImage = PDImageXObject.createFromByteArray(document, IOUtils.toByteArray(inputStream), "monsterCookie");

        contentStream.drawImage(backgroundImage, 0, 0, 600, 400);

        contentStream.beginText();
        contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 20);

        contentStream.setNonStrokingColor(Color.WHITE);

        contentStream.newLineAtOffset(200, 300);

        contentStream.showText("GENERIC APP USER INFO");
        contentStream.newLineAtOffset(0, -50);
        contentStream.showText("Name: " + generatePdfRequest.name());
        contentStream.newLineAtOffset(0, -50);
        contentStream.showText("Surname: " + generatePdfRequest.surname());
        contentStream.newLineAtOffset(0, -50);
        contentStream.showText("Email: " + generatePdfRequest.email());

        contentStream.endText();
    }

    public String generateDocumentTest(GeneratePdfRequest generatePdfRequest) throws IOException {

        PDDocument document = new PDDocument();
        PDRectangle pageSize = new PDRectangle(600, 400);
        PDPage firstPage = new PDPage(pageSize);
        document.addPage(firstPage);

        PDPageContentStream pageContentStream = new PDPageContentStream(document, firstPage);

        addContentToPage(generatePdfRequest, pageContentStream, document);

        pageContentStream.close();
        document.save("C:\\programming\\pdf\\test.pdf");
        document.close();

        return "doc created";
    }

}
