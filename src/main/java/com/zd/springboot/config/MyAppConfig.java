package com.zd.springboot.config;

import com.zd.springboot.service.HelloService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dinnes Zhang
 * @date
 * @Configuration: 指明当前类是配置类，用来替代Spring的配置文件
 * <p>
 * 在配置文件中用<bean></bean>标签来添加组件
 */
@Configuration
public class MyAppConfig {

    //将方法的返回值添加到容器中：容器中这个组件默认的id就是方法名
    @Bean
    public HelloService helloService(){
        System.out.println("This config has been imported!");
        return new HelloService();
    }
}
