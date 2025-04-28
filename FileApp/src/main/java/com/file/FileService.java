package com.file;

import com.util.ResponseMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileService {

    @Value("${app.file-storage-path}")
    private String storageDirectory;

    public void saveFile(MultipartFile fileToSave) throws IOException {
        if (fileToSave == null) {
            throw new NullPointerException(ResponseMessages.FILE_NULL);
        }
        File targetFile = new File(storageDirectory + File.separator + fileToSave.getOriginalFilename());
        if (!Objects.equals(targetFile.getParent(), storageDirectory)) {
            throw new SecurityException(ResponseMessages.UNSUPPORTED_FILE);
        }
        Files.copy(fileToSave.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public File getDownloadFile(String fileName) throws Exception {
        if (fileName == null) {
            throw new NullPointerException(ResponseMessages.FILE_NULL);
        }
        File fileToDownload = new File(storageDirectory + File.separator + fileName);
        if (!Objects.equals(fileToDownload.getParent(), storageDirectory)) {
            throw new SecurityException(ResponseMessages.UNSUPPORTED_FILE);
        }
        if (!fileToDownload.exists()) {
            throw new FileNotFoundException(ResponseMessages.FILE_NOT_FOUND + fileName);
        }
        return fileToDownload;
    }

}
