package com.file;

import com.util.ResponseMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FileServiceTests {

    @InjectMocks
    @Autowired
    private FileService fileService;

    @TempDir
    Path tempDir;

    @Value("${app.file-storage-path}")
    private String storageDirectory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveFile_Success() throws IOException {
        MockMultipartFile fileToSave = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "Test file content".getBytes()
        );

        fileService.saveFile(fileToSave);

        File savedFile = new File(storageDirectory + File.separator + "testfile.txt");
        assertTrue(savedFile.exists(), "The file should be saved successfully");
        assertEquals("Test file content", Files.readString(savedFile.toPath()), "File content should match");
    }

    @Test
    public void testSaveFile_NullFile_ThrowsException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            fileService.saveFile(null);
        });

        assertEquals(ResponseMessages.FILE_NULL, exception.getMessage());
    }

    @Test
    public void testSaveFile_InvalidPath_ThrowsSecurityException() {
        MockMultipartFile fileToSave = new MockMultipartFile(
                "file",
                "../testfile.txt", // Attempting path traversal
                "text/plain",
                "Test file content".getBytes()
        );

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            fileService.saveFile(fileToSave);
        });

        assertEquals(ResponseMessages.UNSUPPORTED_FILE, exception.getMessage());
    }

    @Test
    public void testGetDownloadFile_Success() throws Exception {
        // First, create a file in the storage directory
        Path testFilePath = tempDir.resolve("testfile.txt");
        Files.write(testFilePath, "Test file content".getBytes(), StandardOpenOption.CREATE);

        File fileToDownload = fileService.getDownloadFile("testfile.txt");

        assertNotNull(fileToDownload);
        assertTrue(fileToDownload.exists(), "The file should exist");
        assertEquals("Test file content", Files.readString(fileToDownload.toPath()), "File content should match");
    }

    @Test
    public void testGetDownloadFile_NullFileName_ThrowsException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            fileService.getDownloadFile(null);
        });

        assertEquals(ResponseMessages.FILE_NULL, exception.getMessage());
    }

    @Test
    public void testGetDownloadFile_FileNotFound_ThrowsFileNotFoundException() {
        FileNotFoundException exception = assertThrows(FileNotFoundException.class, () -> {
            fileService.getDownloadFile("nonexistentfile.txt");
        });

        assertEquals(ResponseMessages.FILE_NOT_FOUND + "nonexistentfile.txt", exception.getMessage());
    }

    @Test
    public void testGetDownloadFile_InvalidPath_ThrowsSecurityException() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            fileService.getDownloadFile("../testfile.txt"); // Attempting path traversal
        });

        assertEquals(ResponseMessages.UNSUPPORTED_FILE, exception.getMessage());
    }
}
