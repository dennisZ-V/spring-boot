package com.zd.springboot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dinnes Zhang
 * @date
 */
@RestController
@RequestMapping("hello")
public class HelloController {

    @GetMapping
    public String hello(){
        return "Hello!!";
    }
}
