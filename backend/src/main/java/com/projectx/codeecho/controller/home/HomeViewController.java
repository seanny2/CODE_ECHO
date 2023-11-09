package com.projectx.codeecho.controller.home;

import com.projectx.codeecho.domain.MemberEntity;
import com.projectx.codeecho.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeViewController {
    private final MemberService memberService;
    @GetMapping("/")
    public String home() {
        return "index.html";
    }
}