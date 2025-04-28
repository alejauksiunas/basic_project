package com.project.main.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@RequiredArgsConstructor
@Service
public class ObtainSecretJwtKey {

    @Value("${app.application-secure-properties}")
    private String applicationSecureProperties;

    public String readKey() throws IOException {
        Properties properties = new Properties();
        File file = new File(applicationSecureProperties);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        }
        return properties.getProperty("application.security.jwt.secret-key");
    }
}
