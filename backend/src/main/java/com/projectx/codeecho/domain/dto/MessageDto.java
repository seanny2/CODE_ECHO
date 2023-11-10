package com.projectx.codeecho.domain.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String sender;  // yorkie에서 부여한 아이디 (소켓에 의해 전달되는 전송자)
    private String content; // 메시지 내용
}
