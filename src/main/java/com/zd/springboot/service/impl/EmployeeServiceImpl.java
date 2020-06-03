package com.zd.springboot.service.impl;

import com.zd.springboot.entity.Employee;
import com.zd.springboot.mapper.EmployeeMapper;
import com.zd.springboot.service.EmployeeService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author Dinnes Zhang
 * @date
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;

    @Override
    @Cacheable(value = "emp", key = "#id")
    public Employee getEmpById(Long id) {
        return employeeMapper.getEmpById(id);
    }

    @Override
    public Boolean deleteEmpById(Long id) {
        return employeeMapper.deleteEmpById(id);
    }

    @Override
    public boolean insertEmp(Employee employee) {
        return employeeMapper.insertEmp(employee);
    }

    @Override
    @CachePut(value = "emp", key = "#result.id")
    public Employee updateEmp(Employee employee) {
        employeeMapper.updateEmp(employee);
        return employee;
    }

    @RabbitListener(queues = "dennis.news")
    public void received(Employee employee){
        System.out.println(employee);
    }

    @RabbitListener(queues = "dennis")
    public void received02(Message message){
        System.out.println(message.getBody());
        System.out.println(message.getMessageProperties());
    }
}
