package com.zd.springboot.controller;

import com.zd.springboot.entity.Employee;
import com.zd.springboot.mapper.EmployeeMapper;
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
@RequestMapping("employee")
public class EmployeeController {

    @Autowired
    EmployeeMapper employeeMapper;

    @GetMapping("getById")
    public Callable getById(Long id) {
        return () -> employeeMapper.getEmpById(id);
    }

    @PostMapping("save")
    public Callable save(Employee employee) {
        return () -> employeeMapper.insertEmp(employee);
    }
}
