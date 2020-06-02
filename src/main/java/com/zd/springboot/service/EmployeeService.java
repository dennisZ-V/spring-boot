package com.zd.springboot.service;

import com.zd.springboot.entity.Employee;

public interface EmployeeService {
    Employee getEmpById(Long id);

    Boolean deleteEmpById(Long id);

    boolean insertEmp(Employee employee);

    Employee updateEmp(Employee employee);
}
