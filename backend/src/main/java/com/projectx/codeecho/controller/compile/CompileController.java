package com.projectx.codeecho.controller.compile;

import com.projectx.codeecho.domain.CompileResponse;
import com.projectx.codeecho.service.serviceImpl.CompileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Controller
@RequiredArgsConstructor
public class CompileController {
    private final CompileServiceImpl compileService;

    @PostMapping(value="compile")
    public Map<String, Object> compileCode(@RequestBody Map<String, Object> input) throws Exception {
        Map<String, Object> returnMap = new HashMap<>();

        // compile input code
        Object obj = compileService.compileCode(input.get("code").toString());

        // compile 결과 타입이 String일 경우 컴파일 실패 후 메시지 반환으로 판단하여 처리
        if (obj instanceof String) {
            returnMap.put("result", CompileResponse.FAIL.getText());
            returnMap.put("systemOut", obj.toString());
            return returnMap;
        }

        // 실행 후 결과 전달 받음
        long beforeTime = System.currentTimeMillis();

        // 코드 실행
        Map<String, Object> output = compileService.runObject(obj);
        long afterTime = System.currentTimeMillis();

        // 코드 실행 결과 저장
        returnMap.putAll(output);
        // 소요시간
        returnMap.put("performance", (afterTime - beforeTime));

        return returnMap;
    }


}
