package com.zd.springboot.controller;

import com.zd.springboot.entity.Department;
import com.zd.springboot.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.Callable;

/**
 * @author Dinnes Zhang
 * @date
 */
@RestController
@RequestMapping("department")
public class DepartmentController {

    @Autowired
    DepartmentMapper departmentMapper;

    @GetMapping("getDept")
    public Callable getDept(Long id) {
        return () -> departmentMapper.getDeptById(id);
    }

    @PostMapping("saveDept")
    public Callable saveDept(Department department) {
        return () -> departmentMapper.insertDept(department);
    }

    @PostMapping("updateDept")
    public Callable updateDept(Department department) {
        return () -> departmentMapper.updateDept(department);
    }

    @DeleteMapping("deleteDept")
    public Callable deleteDept(Long id) {
        return () -> departmentMapper.deleteDeptById(id);
    }
}
