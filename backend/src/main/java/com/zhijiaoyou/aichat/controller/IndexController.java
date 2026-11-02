package com.zhijiaoyou.aichat.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

@Controller
@RequestMapping("")
public class IndexController {
//    @GetMapping("")
//    public String index(){
//        return "redirect:/web/login.html";
//    }

    @GetMapping(value = "/api/chat2", produces = "text/html;charset=utf-8")
    public Flux<String> stream2(@RequestParam String msg) {
        // 模拟要输出的字符串数组
        String[] chunks = {"你", "好", "，", "我", "是", "A", "I", "助", "手", "！"};

        // 方式一：从数组创建 Flux 并延迟发送（推荐）
        return Flux.fromArray(chunks)
                .delayElements(Duration.ofMillis(100))  // 每个元素间隔 200ms
                .map(chunk -> {
                    System.out.println(chunk);
                    return chunk;
                }) // 格式化 SSE 格式
                .doOnComplete(() -> System.out.println("done"));
    }
}
