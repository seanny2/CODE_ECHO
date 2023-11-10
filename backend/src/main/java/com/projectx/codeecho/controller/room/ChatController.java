package com.projectx.codeecho.controller.room;

import com.projectx.codeecho.domain.entity.MessageEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/room/chat/{roomName}")
    @SendTo("/topic/room/chat/{roomName}")
    public MessageEntity sendMessage(MessageEntity message) {
        return message;
    }
}