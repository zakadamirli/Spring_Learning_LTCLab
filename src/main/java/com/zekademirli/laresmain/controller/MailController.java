package com.zekademirli.laresmain.controller;

import com.zekademirli.laresmain.service.MailService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor  //look at thi write format --syntax--
public class MailController {

    private final
    MailService mailService;

    @PostMapping("/send-email")
    public String sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String text
    ) {
        mailService.sendSimpleEmail(to, subject, text);
        return "Email sent successfully!";
    }
}

