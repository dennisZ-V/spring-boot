package com.zd.springboot.mapper;

import com.zd.springboot.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper {

    Employee getEmpById(Long id);

    Boolean deleteEmpById(Long id);

    boolean insertEmp(Employee employee);

    boolean updateEmp(Employee employee);
}
