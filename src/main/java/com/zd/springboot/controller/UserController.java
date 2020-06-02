package com.zd.springboot.controller;

import com.zd.springboot.entity.User;
import com.zd.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

/**
 * @author Dinnes Zhang
 * @date
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("getUser")
    public Callable getUser(Long id) {
        return () -> userRepository.findById(id);
    }

    @PostMapping("save")
    public Callable save(User user) {
        return () -> userRepository.save(user);
    }
}
