package com.zd.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

/**
 * @author Dinnes Zhang
 * @date
 */
@RestController
@RequestMapping("hello")
public class HelloController {

    @GetMapping
    public Callable hello() {
        return () -> LocalDateTime.now();
    }
}
