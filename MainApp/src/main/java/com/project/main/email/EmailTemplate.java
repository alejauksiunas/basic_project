package com.project.main.email;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailTemplate {

    //TODO: email builder
    public String buildEmail(String name, String link) {
        return " <p> Hi " + name + ",</p>" +
                "<p> Click on the link below to activate your account: </p> " +
                "<p> <a href=\"" + link + "\">Activate Now</a> </p>" +
                "<p> Link will expire in 15 minutes. </p>";
    }
}
