package com.projectx.codeecho.controller.room;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RoomViewController {
    @GetMapping("/room")
    public String room() {
        return "../static/room.html";
    }

}