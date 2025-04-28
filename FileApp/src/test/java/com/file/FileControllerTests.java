package com.file;

import com.file.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class FileControllerTests {

    @Value("${app.file-storage-path}")
    private String storageDirectory;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    private FileService fileService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testUploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "testfile.txt", "text/plain", "Test file content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files/upload") // Updated path
                .file(file))
                .andExpect(status().isOk());
    }

    @Test
    public void testUploadFile_Failure() throws Exception {
        doThrow(new RuntimeException("Upload failed")).when(fileService).saveFile(any());

        MockMultipartFile file = new MockMultipartFile("file", "testfile.txt", "text/plain", "Test file content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files/upload")
                .file(file))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testDownloadFile_Success() throws Exception {
        String filename = "testfile.txt";
        File file = new File(storageDirectory + filename);
        Files.createFile(Paths.get(file.toURI())); // Ensure file exists for the test

        when(fileService.getDownloadFile(filename)).thenReturn(file);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/download")
                .param("fileName", filename))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\""))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_OCTET_STREAM));

        Files.delete(Paths.get(file.toURI())); // Cleanup
    }

    @Test
    public void testDownloadFile_Failure() throws Exception {
        String filename = "testfile.txt";

        when(fileService.getDownloadFile(filename)).thenThrow(new RuntimeException("File not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/files/download")
                .param("fileName", filename))
                .andExpect(status().isNotFound());
    }
}
