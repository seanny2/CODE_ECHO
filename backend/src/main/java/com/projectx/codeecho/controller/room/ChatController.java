package com.projectx.codeecho.controller.room;

import com.projectx.codeecho.domain.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ChatController {
    @GetMapping(value="/chat/{roomName}")
    public ModelAndView chatView(@PathVariable String roomName) {
        ModelAndView mv = new ModelAndView("/chat");
        mv.addObject("roomName", roomName); // 방의 이름을 뷰로 전달
        return mv;
    }

    @MessageMapping("/chat/{roomName}")
    @SendTo("/topic/room/{roomName}")
    public Message sendMessage(Message message) {
        return message;
    }
}