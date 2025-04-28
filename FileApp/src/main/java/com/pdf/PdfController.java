package com.pdf;

import com.security.JwtValidator;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path = "api/documents")
@AllArgsConstructor
public class PdfController {
    private final PdfService pdfService;
    private final JwtValidator jwtValidator;

    //TODO: only to play around with document content without any authorization
    @PostMapping("/generate/test")
    public String generateString(@RequestBody GeneratePdfRequest generatePdfRequest) throws IOException {
        return pdfService.generateDocumentTest(generatePdfRequest);
    }

    @PostMapping(value = "/generate/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generate(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody GeneratePdfRequest generatePdfRequest) throws IOException
    {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            jwtValidator.validateToken(authorizationHeader.substring(7));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        byte[] pdfBytes = pdfService.generateDocumentPdf(generatePdfRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", generatePdfRequest.name() + ".pdf");
        headers.setContentLength(pdfBytes.length);
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
