package com.pdf;

import com.security.JwtValidator;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class PdfControllerTests {

    @InjectMocks
    @Autowired
    private PdfController pdfController;

    @Mock
    @Autowired
    private JwtValidator jwtValidator;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        doNothing().when(jwtValidator).validateToken(anyString());
    }

    @Test
    public void testGenerate_Unauthorized_MissingAuthorizationHeader() throws IOException {
        ResponseEntity<byte[]> response = pdfController.generate(null, new GeneratePdfRequest(null, null, null));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody(), "The response body should be null.");
    }

    @Test
    public void testGenerate_Unauthorized_InvalidAuthorizationHeader() throws IOException {
        ResponseEntity<byte[]> response = pdfController.generate("InvalidToken", new GeneratePdfRequest(null, null, null));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody(), "The response body should be null.");
    }

    @Test
    public void testGenerate_Unauthorized_JwtValidationFails() throws IOException {
        doThrow(new JwtException("Invalid token")).when(jwtValidator).validateToken(anyString());

        ResponseEntity<byte[]> response = pdfController.generate("Bearer someInvalidToken", new GeneratePdfRequest(null, null, null));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody(), "The response body should be null.");
    }
}
