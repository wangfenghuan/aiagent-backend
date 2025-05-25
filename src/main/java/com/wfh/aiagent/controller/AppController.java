package com.wfh.aiagent.controller;

import cn.hutool.core.util.RandomUtil;
import com.wfh.aiagent.app.ProgramingApp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/test")
    public String test(){
        String string1 = RandomUtil.randomNumbers(5);
        String string = programingApp.doChat("你好啊", string1);
        String string2 = programingApp.doChat("我叫王凤欢", string1);
        String string3 = programingApp.doChat("我叫什么，我记得我给你说过啊", string1);
        String string4 = programingApp.doChat("我叫什么", string1);
        System.out.println(string);
        System.out.println(string2);
        System.out.println(string3);
        System.out.println(string4);
        return string3;
    }
}
