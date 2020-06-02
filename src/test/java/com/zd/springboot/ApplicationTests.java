package com.zd.springboot;

import com.zd.springboot.entity.Employee;
import com.zd.springboot.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class ApplicationTests {

    @Autowired
    DataSource dataSource;

    //操作k-v字符串
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    //操作k-v都是对象
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    RedisTemplate<String, Object> empRedisTemplate;

    @Test
    void contextLoads() throws SQLException {
        System.out.println(dataSource.getClass());

        Connection connection = dataSource.getConnection();

        System.out.println(connection);
        connection.close();
    }

    @Test
    void testRedis() {
        Employee employee = employeeMapper.getEmpById((long) 2);
        //默认如果保存对象，使用jdk序列化机制，序列化后的数据保存到redis
        //1、将数据以json方式保存
        //1）自己将对象转为json
        //2）redisTemplate默认序列化规则,改变默认的序列化规则
        empRedisTemplate.opsForValue().set("com:dennis:test02", employee);
    }

}
