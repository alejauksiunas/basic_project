package com.pdf;

import org.apache.pdfbox.Loader;
import org.springframework.boot.test.context.SpringBootTest;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PdfServiceTests {
    @InjectMocks
    private PdfService pdfService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateDocumentPdf_Success() throws IOException {
        GeneratePdfRequest request = new GeneratePdfRequest("Test", "Test", "test.test@test.lt");

        byte[] pdfBytes = pdfService.generateDocumentPdf(request);

        assertNotNull(pdfBytes, "The generated PDF byte array should not be null.");
        assertTrue(pdfBytes.length > 0, "The generated PDF byte array should not be empty.");

        PDDocument document = Loader.loadPDF(pdfBytes);
        assertNotNull(document, "The generated PDF should be a valid document.");
        assertEquals(1, document.getNumberOfPages(), "The PDF should contain one page.");
        document.close();
    }

    @Test
    public void testGenerateDocumentPdf_EmptyRequest() throws IOException {
        GeneratePdfRequest request = new GeneratePdfRequest(null, null, null);

        byte[] pdfBytes = pdfService.generateDocumentPdf(request);

        assertNotNull(pdfBytes, "The generated PDF byte array should not be null.");
        assertTrue(pdfBytes.length > 0, "The generated PDF byte array should not be empty.");

        PDDocument document = Loader.loadPDF(pdfBytes);
        assertNotNull(document, "The generated PDF should be a valid document.");
        assertEquals(1, document.getNumberOfPages(), "The PDF should contain one page.");
        document.close();
    }

    @Test
    public void testGenerateDocumentPdf_NullRequest() {
        GeneratePdfRequest request = null;

        assertThrows(NullPointerException.class, () -> {
            pdfService.generateDocumentPdf(request);
        }, "NullPointerException should be thrown when the request is null.");
    }
}

