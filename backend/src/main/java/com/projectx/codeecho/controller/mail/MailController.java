package com.projectx.codeecho.controller.mail;

import com.projectx.codeecho.domain.dto.MailDto;
import com.projectx.codeecho.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class MailController {
    private final MailService emailService;

    @PostMapping("/share")
    public ResponseEntity<String> execMail(@RequestBody MailDto mailDto){
        if (emailService.sendSimpleMessage(mailDto)) {
            return new ResponseEntity<>("링크 공유에 성공 했습니다.", HttpStatus.OK);
        }
        return new ResponseEntity<>("링크 공유에 실패 했습니다.", HttpStatus.NOT_FOUND);
    }
}