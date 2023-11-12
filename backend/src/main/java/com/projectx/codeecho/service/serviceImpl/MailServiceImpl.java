package com.projectx.codeecho.service.serviceImpl;

import com.projectx.codeecho.domain.dto.MailDto;
import com.projectx.codeecho.service.MailService;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailServiceImpl implements MailService {
    private JavaMailSender emailSender;
    public boolean sendSimpleMessage(MailDto mailDto){
        try {
            String link = mailDto.getContent();
            for (String toAddress : mailDto.getToAddress()) {
                MimeMessage message = emailSender.createMimeMessage();
                message.addRecipients(Message.RecipientType.TO, toAddress);
                message.setSubject("코드 에코와 함께 할 준비가 되었나요?");
                String content = "";
                content += "<div style='margin:100px;'>";
                content += "<a href='http://52.78.5.44/?roomid="+link+"&nickname="+toAddress.split("@")[0]+"'>참여하기</a>";
                content += "</div>";
                message.setText(content, "utf-8", "html");
                message.setFrom(new InternetAddress("Code-echo@gmail.com", "codeecho"));

//                SimpleMailMessage message = new SimpleMailMessage();
//                message.setFrom("Code-echo@gmail.com");
//                message.setTo(toAddress);
//                message.setSubject("코드 에코와 함께 할 준비가 되었나요?"); // add please
//                message.setText("http://localhost:8083/?roomid="+link+"&nickname="+toAddress.split("@")[0]); // add link please
                emailSender.send(message);
            }
            System.out.printf("[%s]: 링크 공유에 성공했습니다.\n", MailService.class);
            return true;
        } catch (Exception e) {
            System.out.printf("[%s]: 링크 공유에 실패했습니다.\n", MailService.class);
            return false;
        }
    }
}