package com.zd.springboot;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class ApplicationTests {

    @Autowired
    ApplicationContext ioc;

    Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testHello(){
        Boolean result = ioc.containsBean("helloService");
        System.out.println(result);
    }

    @Test
    void contextLoads() {
        //日志的级别
        //由高到低
        //可以调整日志的级别，日志就只会在这个级别以后的高级别生效
        logger.trace(() -> "这是一个trace");

        logger.debug(() -> "这是一个debug");

        logger.info(() -> "这是一个info");

        logger.warn(() -> "这是一个warn");

        logger.error(() -> "这是一个error");
    }

}
