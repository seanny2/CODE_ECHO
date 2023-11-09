package com.projectx.codeecho.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private String sender;
    private LocalDateTime timestamp;

    // 생성자, getter 및 setter 메서드
    public String getText(){
        return text;
    }
    public String getSender(){
        return sender;
    }


    // 기본 생성자
    public Message() {}

    public Message(String text, String sender, LocalDateTime timestamp) {
        this.text = text;
        this.sender = sender;
        this.timestamp = timestamp;
    }
}