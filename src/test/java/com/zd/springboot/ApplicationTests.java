package com.zd.springboot;

import com.zd.springboot.entity.Employee;
import com.zd.springboot.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

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

    @Autowired
    RabbitTemplate rabbitTemplate;

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

    @Test
    void testMq(){
        //Message需要自己构造，定义消息体内容和消息头
//        rabbitTemplate.send(exchange,routingKey,message);
        //object默认当成消息体，只需要传入要发送的对象，自动序列化发送给mq
//        rabbitTemplate.convertAndSend(exchange,routingKey,object);
        Map<String,Object> map = Map.of("msg","这是第一个消息","data", Arrays.asList("helloworld",123,true));
        //对象被默认序列化以后发送
        rabbitTemplate.convertAndSend("exchange.direct","dennis.news",map);
    }

    @Test
    void receive(){
        //接收数据，如何将数据自动的转为json发送
        Object o = rabbitTemplate.receiveAndConvert("dennis.news");
        System.out.println(o.getClass());
        System.out.println(o);
    }

}
