package com.projectx.codeecho.service;

import com.projectx.codeecho.domain.dto.MailDto;

public interface MailService {
    boolean sendSimpleMessage(MailDto mailDto);
}
