package com.projectx.codeecho.controller.room;

import com.projectx.codeecho.domain.dto.MessageDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ChatController {

    @MessageMapping("/room/chat/{roomName}")
    @SendTo("/topic/room/chat/{roomName}")
    public MessageDto sendMessage(@RequestBody MessageDto message) {
        return message;
    }
}