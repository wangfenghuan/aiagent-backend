package com.wfh.aiagent.controller;

import com.wfh.aiagent.app.ProgramingApp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;

import java.io.IOException;

/**
 * @Author FengHuan Wang
 * @Date 2025/5/25 14:51
 * @Version 1.0
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Resource
    private ProgramingApp programingApp;

    @PostMapping(value = "/chat")
    public SseEmitter test(String message, String chatId){
        SseEmitter emitter = new SseEmitter();
        // 服务端推送
        Disposable subscribe = programingApp.doChatWithRagToolStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        emitter.send(chunk);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                }, emitter::completeWithError, emitter::complete);
        emitter.onCompletion(subscribe::dispose);
        return emitter;
    }

    @GetMapping("/health")
    public String health(){
        return "ok";
    }
}
