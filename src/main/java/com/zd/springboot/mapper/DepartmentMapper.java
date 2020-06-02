package com.zd.springboot.mapper;

import com.zd.springboot.entity.Department;
import org.apache.ibatis.annotations.*;

/**
 * @author Dinnes Zhang
 * @date
 *
 * @Mapper 指定这是一个操作数据库的mapper
 */
@Mapper
public interface DepartmentMapper {

    @Select("select * from department where id = #{id}")
    Department getDeptById(Long id);

    @Delete("delete from department where id = #{id}")
    Boolean deleteDeptById(Long id);

    @Insert("insert into department (department_name) values (#{departmentName})")
    boolean insertDept(Department department);

    @Update("update department set department_name=#{departmentName} where id = #{id}")
    boolean updateDept(Department department);
}
