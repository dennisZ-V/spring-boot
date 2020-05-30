package com.zd.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @author Dinnes Zhang
 * @date
 */
@Controller
public class HelloController {

    @GetMapping("start")
    public Callable hello() {
        return () -> LocalDateTime.now();
    }

    @RequestMapping("success")
    public String success(Map<String, Object> map) {
        map.put("hello", "你好");
        return "success";
    }
}
