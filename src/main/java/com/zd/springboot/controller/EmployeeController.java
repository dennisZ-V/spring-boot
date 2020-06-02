package com.zd.springboot.controller;

import com.zd.springboot.entity.Employee;
import com.zd.springboot.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.Callable;

/**
 * @author Dinnes Zhang
 * @date
 */
@RestController
@RequestMapping("employee")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @GetMapping("getById")
    public Callable getById(Long id) {
        return () -> employeeService.getEmpById(id);
    }

    @PostMapping("save")
    public Callable save(Employee employee) {
        return () -> employeeService.insertEmp(employee);
    }

    @PutMapping("update")
    public Callable update(Employee employee){
        return () -> employeeService.updateEmp(employee);
    }

    @DeleteMapping("delete")
    public Callable delete(Long id){
        return ()-> employeeService.deleteEmpById(id);
    }
}
