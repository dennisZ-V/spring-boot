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



#### DAY 2

##### @PropertySource&@ImportResource

**@PropertySource** : 加载指定的配置文件

```java
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
```

```properties
person.last-name=dennis
person.age=12
person.birth=2020/05/28
person.boss=false
person.maps.k1=v1
person.maps.k2=14
person.lists=a,b,c
person.dog.name=beibei
person.dog.age=2
```

**@ImportResource** : 导入Spring的配置文件，让配置文件里面的内容生效

Spring Boot里面没有Spring的配置文件，我们自己编写的配置文件，也不能自动识别；

如让Spring配置文件生效，需要把**@ImportResource**标注在配置类上

```java
@ImportResource(locations = {"classpath:beans.xml"})
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
--------------------------------------------------------------------------------
@Autowired
ApplicationContext ioc;

@Test
public void testHello(){
	Boolean result = ioc.containsBean("helloService");
	System.out.println(result);
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd">
    <bean id="helloService" class="com.zd.springboot.service.HelloService"></bean>
</beans>
```

SpringBoot推荐给容器中添加组件的方式：使用全注解的方式

1、配置类===========Spring配置文件

2、使用@Bean给容器中添加组件

```java
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
```






