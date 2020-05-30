package com.zd.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Dinnes Zhang
 * @date
 * @Configuration: 指明当前类是配置类，用来替代Spring的配置文件
 * <p>
 * 在配置文件中用<bean></bean>标签来添加组件
 */
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    //将方法的返回值添加到容器中：容器中这个组件默认的id就是方法名
//    @Bean
//    public HelloService helloService(){
//        System.out.println("This config has been imported!");
//        return new HelloService();
//    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //浏览器发送/test请求也来到成功页面
        registry.addViewController("/test").setViewName("success");
    }
}
