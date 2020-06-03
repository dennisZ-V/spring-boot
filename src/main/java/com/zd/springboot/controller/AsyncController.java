package com.zd.springboot.controller;

import com.zd.springboot.service.impl.AsyncServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dinnes Zhang
 * @date
 */
@RestController
public class AsyncController {

    @Autowired
    AsyncServiceImpl asyncService;

    @GetMapping("hello")
    public String hello(){
        asyncService.hello();
        return "success";
    }
}
