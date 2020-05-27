# spring-boot
#### Day 1

| 功能                       | @ConfigurationProperties | @Value   |
| -------------------------- | ------------------------ | -------- |
| 批量注入配置文件中的属性   | 支持                     | 逐个指定 |
| 松散语法绑定               | 支持                     | 不支持   |
| SpEL                       | 不支持                   | 支持     |
| JSR303数据校验(@Validated) | 支持                     | 不支持   |
| 复杂类型封装               | 支持                     | 不支持   |

如果只是在某个业务逻辑中需要获取配置文件中的某项值时，使用@Value

```java
@Value("${person.last-name}")
private String name;

@GetMapping("sayHello")
public String sayHello(){
	return "Hello" + name;
}
```

如果专门编写了一个javaBean来和配置文件进行映射时，使用@ConfigurationProperties




