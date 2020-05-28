package com.zd.springboot.entity;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author Dinnes Zhang
 * @date
 */
@Data
@Component
@ConfigurationProperties(prefix = "person")
@PropertySource(value = {"classpath:person.properties"})
public class Person {
    private String lastName;
    private Integer age;
    private Boolean boss;
    private LocalDate birth;

    private Map<String, Object> maps;
    private List<Object> lists;
    private Dog dog;
}
