package com.projectx.codeecho.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MailDto {
    private String[] toAddress;
    private String content;
}