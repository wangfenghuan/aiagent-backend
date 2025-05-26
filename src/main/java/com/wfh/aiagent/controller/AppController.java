package com.wfh.aiagent.controller;

import cn.hutool.core.util.RandomUtil;
import com.wfh.aiagent.app.ProgramingApp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/test")
    public String test(String message){
        String code = RandomUtil.randomNumbers(5);
        String string = programingApp.doChatWithRag(message, code);
        return string;
    }
}
