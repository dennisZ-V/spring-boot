# spring-boot
### Day 1

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



### DAY 2

#### 一、@PropertySource&@ImportResource

**@PropertySource** : 加载指定的配置文件（不支持yml格式的配置文件）

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

#### 二、配置文件占位符

##### 1）随机数

```java
${random.value}、${random.int}、${random.long}
${random.int(10)}、${random.int[1024,65536]}
```

##### 2）占位符获取之前配置的值，如果没有可以使用：指定默认值

#### 三、Profile

**概念**：Profile是Spring对不同环境提供不同配置功能的支持，可以通过激活、指定参数等方式快速切换环境。

##### 1）多Profile文件

我们在主配置文件编写的时候，文件名可以是 application-(profile).properties

默认使用application.properties的配置

##### 2）yml支持多文档块方式

```yml
server:
  port: 9090
spring:
  profiles:
    active: prod
---
server:
  port: 9091
spring:
  profiles: dev
---
server:
  port: 9094
spring:
  profiles: prod
```



##### 3）激活指定Profile

① 在配置文件中指定spring.profile.active=dev

② 命令行：--spring.profiles.active=dev

![image-20200529102422170](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200529102422170.png)

③ 虚拟机参数：-Dspring.profiles.active=dev

![image-20200529102950086](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200529102950086.png)

### DAY 3

#### 一、配置文件加载位置

spring boot启动会扫描以下位置的application.properties或者application.yml文件作为Spring boot的默认配置文件

-file:./config/

-file:./

-classpath:/config/

-classpath:/

-以上是按照<font color=red>**优先级从高到低**</font>的顺序，所有位置的文件都会被加载，**高优先级配置**内容会覆盖**低优先级配置**内容。

-我们也可以通过配置spring.config.location来改变默认配置

#### 二、外部配置加载顺序

Spring Boot 支持多种外部配置方式

这些方式优先级如下：

https://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/#boot-features-external-config

### DAY4

配置文件到低能写什么？怎么写？自动配置原理：

[配置文件能配置的属性参照](https://docs.spring.io/spring-boot/docs/1.5.9.RELEASE/reference/htmlsingle/#common-application-properties)



#### 一、自动配置原理：

1）、SpringBoot启动的时候加载主配置类，开启了自动配置功能**@EnableAutoConfiguration**

2）、**@EnableAutoConfiguration**作用：

- 利用AutoConfigurationImportSelector给容器中导入一些组件

- 可以查看selectImports()方法的内容

- ```java
  //获取候选的配置
  List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
  ```

  - ```java
    SpringFactoriesLoader.loadFactoryNames()
    //扫描所有jar包类路径下的 META-INF/spring.factories
    //把扫描到的这些文件的内容
    //从properties中获取到EnableAutoConfiguration.class类（类名）对应的值，然后把它们添加在容器中
    ```

  **将类路径下 META-INF/spring.factories里面配置的所有EnableAutoConfiguration的值加入到了容器中**

  ```properties
  # Auto Configure
  org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
  org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
  org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration,\
  org.springframework.boot.autoconfigure.batch.BatchAutoConfiguration,\
  org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration,\
  org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration,\
  org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration,\
  org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration,\
  org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration,\
  org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration,\
  org.springframework.boot.autoconfigure.couchbase.CouchbaseAutoConfiguration,\
  org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.cassandra.CassandraRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.couchbase.CouchbaseDataAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.couchbase.CouchbaseReactiveDataAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.couchbase.CouchbaseReactiveRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.couchbase.CouchbaseRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRestClientAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.ldap.LdapRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.neo4j.Neo4jRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.solr.SolrRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.r2dbc.R2dbcTransactionManagerAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration,\
  org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration,\
  org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration,\
  org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration,\
  org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration,\
  org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration,\
  org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration,\
  org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration,\
  org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration,\
  org.springframework.boot.autoconfigure.hazelcast.HazelcastAutoConfiguration,\
  org.springframework.boot.autoconfigure.hazelcast.HazelcastJpaDependencyAutoConfiguration,\
  org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration,\
  org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration,\
  org.springframework.boot.autoconfigure.influx.InfluxDbAutoConfiguration,\
  org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration,\
  org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration,\
  org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration,\
  org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,\
  org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration,\
  org.springframework.boot.autoconfigure.jdbc.JndiDataSourceAutoConfiguration,\
  org.springframework.boot.autoconfigure.jdbc.XADataSourceAutoConfiguration,\
  org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration,\
  org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration,\
  org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration,\
  org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration,\
  org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration,\
  org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration,\
  org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration,\
  org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration,\
  org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration,\
  org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration,\
  org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration,\
  org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapAutoConfiguration,\
  org.springframework.boot.autoconfigure.ldap.LdapAutoConfiguration,\
  org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration,\
  org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration,\
  org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration,\
  org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration,\
  org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration,\
  org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration,\
  org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration,\
  org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,\
  org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration,\
  org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration,\
  org.springframework.boot.autoconfigure.rsocket.RSocketMessagingAutoConfiguration,\
  org.springframework.boot.autoconfigure.rsocket.RSocketRequesterAutoConfiguration,\
  org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration,\
  org.springframework.boot.autoconfigure.rsocket.RSocketStrategiesAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.rsocket.RSocketSecurityAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyAutoConfiguration,\
  org.springframework.boot.autoconfigure.sendgrid.SendGridAutoConfiguration,\
  org.springframework.boot.autoconfigure.session.SessionAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration,\
  org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration,\
  org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration,\
  org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration,\
  org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration,\
  org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration,\
  org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration,\
  org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration,\
  org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.reactive.function.client.ClientHttpConnectorAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration,\
  org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration,\
  org.springframework.boot.autoconfigure.websocket.reactive.WebSocketReactiveAutoConfiguration,\
  org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration,\
  org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration,\
  org.springframework.boot.autoconfigure.webservices.WebServicesAutoConfiguration,\
  org.springframework.boot.autoconfigure.webservices.client.WebServiceTemplateAutoConfiguration
  ```

  每一个这样的 xxxAutoConfiguration类都是容器的一个组件，都加入到容器中，用它们来自动配置。

  3）、每一个自动配置类进行自动配置功能

  4）、以**HttpEncodingAutoConfiguration**为例解释自动配置原理

  ```java
  //表示这是一个配置类，也可以给容器添加组件
  @Configuration(proxyBeanMethods = false)
  //启用指定类的ConfigurationProperties功能，将配置文件对用的值和HttpEncodingAutoConfiguration绑定起来
  @EnableConfigurationProperties(ServerProperties.class)
  //Spring底层@Conditional注解，根据不同的条件，如果满足指定的条件，整个配置类里面的配置就会生效（判断当前应用是否是web应用，如果是，当前配置类生效）
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  //判断当前项目有没有这个类CharacterEncodingFilter：SpringMVC中进行乱码解决的过滤器
  @ConditionalOnClass(CharacterEncodingFilter.class)
  //判断配置文件中是否存在某个配置 server.servlet.encoding.enabled 
  //matchIfMissing如果不存在判断也是成立的，即使配置文件中不配置enabled属性，也是默认生效的
  @ConditionalOnProperty(prefix = "server.servlet.encoding", value = "enabled", matchIfMissing = true)
  public class HttpEncodingAutoConfiguration {
      
      //已经和SpringBoot的配置文件映射了
      private final Encoding properties;
      
      //只有一个有参构造器的情况下，参数的值就会从容器中拿
      public HttpEncodingAutoConfiguration(ServerProperties properties) {
  		this.properties = properties.getServlet().getEncoding();
  	}
      
      //给容器中添加一个组件，这个组件的某些值需要从properties中获取
      @Bean
  	@ConditionalOnMissingBean
  	public CharacterEncodingFilter characterEncodingFilter() {
  		CharacterEncodingFilter filter = new OrderedCharacterEncodingFilter();
  		filter.setEncoding(this.properties.getCharset().name());
  		filter.setForceRequestEncoding(this.properties.shouldForce(Encoding.Type.REQUEST));
  		filter.setForceResponseEncoding(this.properties.shouldForce(Encoding.Type.RESPONSE));
  		return filter;
  	}
  ```

  根据当前不同的条件判断，决定这个配置类是否生效

  一旦这个配置类生效：这个配置类就会给容器中添加各种组件，这些组件的属性是从对应的properties类中获取，这些类里面的每一个属性又是和配置文件绑定

  

  5）、所有在配置文件中的配置的属性都是在xxxProperties类中封装，配置文件能配置什么就可以参照某一个功能对应的这个属性类

  ```java
  @ConfigurationProperties(prefix = "server", ignoreUnknownFields = true)//从配置文件中获取指定的值和bean
  public class ServerProperties {
  ```

  

**精髓**

​	**1）、SpringBoot启动会加载大量的自动配置类**

​	**2）、我们看我们需要的功能有没有SpringBoot默认写好的自动配置类**

​	**3）、我们再看这个自动配置类中到低配置了哪些组件（只要我们要用的组件有，我们就不需要再来配置）**

​	**4）、给容器中自动配置类添加组建的时候，会从properties类中获取某些属性，我们就可以在配置文件中指定这些属性的值**

xxxxxxAutoConfiguration： 自动配置类

给容器中添加组件

xxxxxxProperties：封装配置文件中相关属性



#### 二、细节

##### @Conditional派生注解（Spring注解版原生的@Conditional作用）

作用：必须是@Conditional指定的条件成立，才给容器中添加组件，配置类里面的所有内容才生效

| @Conditional扩展直接            | 作用（判断是否满足当前指定条件）                 |
| ------------------------------- | ------------------------------------------------ |
| @ConditionalOnJava              | 系统的java版本是否符合要求                       |
| @ConditionalOnBean              | 容器中存在指定Bean                               |
| @ConditionalOnMissingBean       | 容器中不存在指定Bean                             |
| @ConditionalOnExpression        | 满足SpEL表达式指定                               |
| @ConditionalOnClass             | 系统中有指定的类                                 |
| @ConditionalOnMissingClass      | 系统中没有指定的类                               |
| @ConditionalOnSingleCandidate   | 容器中只有一个指定的Bean，或者这个Bean是首选Bean |
| @ConditionalOnProperty          | 系统中指定的属性是否有指定的值                   |
| @ConditionalOnResource          | 类路径下是否存在指定资源文件                     |
| @ConditionalOnWebApplication    | 当前是web环境                                    |
| @ConditionalOnNotWebApplication | 当前不是web环境                                  |
| @ConditionalOnJndi              | JNDI存在指定项                                   |

自动配置类必须在一定的条件下才能生效

我们可以通过启用 debug=true属性，来让控制台打印自动配置报告，这样就可以很方便的知道哪些自动配置类生效

#### 三、日志

##### 日志框架

| 日志门面（日志的抽象层）                                     | 日志实现                                           |
| ------------------------------------------------------------ | -------------------------------------------------- |
| ~~JCL(Jakarta Commons Loggin)~~        SLF4J(Simple Logging Facade for Java)     ~~Jboss-logging~~ | ~~Log4J~~   JUL(java.util.loggin)  Log4j2  Logback |

左边选一个门面（抽象层）、右边来选一个实现：

日志门面：SLF4J

日志实现：Logback



SpringBoot：底层是Spring框架，Spring框架默认使用JCL

​	**SpringBoot选用SLF4J和Logback**

##### SLF4J使用

###### 1）、如何在系统中使用SLF4J

以后开发的时候，日志记录方法的调用，不应该来直接调用日志的实现类，而是调用日志抽象层里的方法

给系统里面导入slf4j的jar和logback的实现jar

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.info("Hello World");
  }
}
```

![image-20200530144626599](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200530144626599.png)

每一个日志的实现框架都有自己的配置文件，配置文件使用SLF4J以后，配置文件还是做成日志实现框架的配置文件

##### SpringBoot日志关系

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <version>2.3.0.RELEASE</version>
    <scope>compile</scope>
</dependency>
```

SpringBoot使用它来做日志功能

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-logging</artifactId>
    <version>2.3.0.RELEASE</version>
    <scope>compile</scope>
</dependency>
```

底层依赖关系

![image-20200530150132346](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20200530150132346.png)

总计：

​	1）、SpringBoot底层也是使用SLF4J+Logback的方式进行日志记录

​	2）、SpringBoot也把其他的日志都替换成了SLF4J

​	3）、中间替换包

#### 四、日志使用

##### 	默认配置

​	SpringBoot默认帮我们配置好了日志：

```java
Logger logger = LoggerFactory.getLogger(getClass());

@Test
void contextLoads() {
    //日志的级别
    //由高到低
    //可以调整日志的级别，日志就只会在这个级别以后的高级别生效
    //传参是Supplier<String>类型，使用Lambda表达式声明
    logger.trace(() -> "这是一个trace");
    logger.debug(() -> "这是一个debug");
    logger.info(() -> "这是一个info");
    logger.warn(() -> "这是一个warn");
    logger.error(() -> "这是一个error");
}
```

```yml
logging.level:
  com.zd.springboot: trace
```

#### 五、Web开发

##### 一、简介

使用SpringBoot：

**1）、创建SpringBoot应用，选中我们需要的模块**

**2）、SpringBoot已经默认将这些场景配置好了，只需要在配置文件中指定少量配置就可运行起来**

**3）、自己编写业务代码**



**自动配置原理**

这个创景SpringBoot帮我们配置了什么？能不能修改？能修改哪些配置？能不能扩展？

```java
xxxAutoConfiguration：帮我们给容器中自动配置组件
xxxProperties：配置类来封住配置文件的内容
```



##### 二、SpringBoot对静态资源的映射规则

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
	if (!this.resourceProperties.isAddMappings()) {
		logger.debug("Default resource handling disabled");
		return;
	}
	Duration cachePeriod = this.resourceProperties.getCache().getPeriod();
	CacheControl cacheControl = this.resourceProperties.getCache().getCachecontrol().toHttpCacheControl();
	if (!registry.hasMappingForPattern("/webjars/**")) {
		customizeResourceHandlerRegistration(registry.addResourceHandler("/webjars/**")
				.addResourceLocations("classpath:/META-INF/resources/webjars/")
				.setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
	}
	String staticPathPattern = this.mvcProperties.getStaticPathPattern();
	if (!registry.hasMappingForPattern(staticPathPattern)) {
		customizeResourceHandlerRegistration(registry.addResourceHandler(staticPathPattern)
				.addResourceLocations(getResourceLocations(this.resourceProperties.getStaticLocations()))
				.setCachePeriod(getSeconds(cachePeriod)).setCacheControl(cacheControl));
	}
}
```

1、所有/webjars/**，都去classpath:/META-INF/resources/webjars/找资源

​	[webjars](https://www.webjars.org/)：以jar包的方式引入静态资源

```xml
<!--引入jquery-webjar-->在访问的时候只需要写webjars下面资源的名称即可
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.5.1</version>
</dependency>
```

2、"/**"访问当前项目的任何资源（静态资源的文件夹）

```java
"classpath:/META-INF/resources/"
"classpath:/resources/"
"classpath:/static/" 
"classpath:/public/"
"/":当前项目的根路径
```

localhost:8080/abc====去静态资源文件夹里面找abc

3、欢迎页：静态资源文件夹下的所有index.html页面，被"/**"映射

localhost:8080/	找index页面

##### 三、模板引擎

Jsp, Velocity, Freemarker, Thymeleaf

SpringBoot推荐使用Thymeleaf

语法更简单，功能更强大

###### 1、引入Thymeleaf

```xml
<dependency>
	<groupId>org.thymeleaf</groupId>
	<artifactId>thymeleaf</artifactId>
</dependency>
切换thymeleaf版本
<thymeleaf.version>x.x.x</thymeleaf.version>
<thymeleaf-layout-dialect.version>x.x.x</thymeleaf-layout-dialect.version>
```

###### 2、Thymeleaf使用&语法

```java
@ConfigurationProperties(prefix = "spring.thymeleaf")
public class ThymeleafProperties {

	private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

	public static final String DEFAULT_PREFIX = "classpath:/templates/";

	public static final String DEFAULT_SUFFIX = ".html";
    //只要把html页面放在classpath:/templates/ thymeleaf就能自动渲染
```

使用：

1）、导入Thymeleaf的名称空间

```html
<html lang="en" xmlns:th="http://www.thymeleaf.org">
```

2）、使用Thymeleaf语法

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <h1>成功</h1>
<!--th:text 将div里面的文本内容设置为-->
    <div th:text="${hello}">这是显示欢迎信息</div>
</body>
</html>
```

3）、语法规则

- ​	th:text：改变当前元素里面的文本内容

  th：任意html属性：来替换原生属性的值

##### 四、SpringMVC的自动配置

###### 1、SpringMVC auto-configuration

https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications

Spring Boot provides auto-configuration for Spring MVC that works well with most applications.

The auto-configuration adds the following features on top of Spring’s defaults:

- Inclusion of `ContentNegotiatingViewResolver` and `BeanNameViewResolver` beans.
  - ContentNegotiatingViewResolver：组合所有的视图解析器
  - 如何定制：我们可以自己给容器中添加一个视图解析器，自动将其组合进来
- Support for serving static resources, including support for WebJars (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-static-content))).
- Automatic registration of `Converter`, `GenericConverter`, and `Formatter` beans.
  - `Converter`：转换器 	public String hello(User user);	类型转换使用
  - `Formatter`：格式化器   2020-05-30===Date
- Support for `HttpMessageConverters` (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-message-converters)).
  - `HttpMessageConverters` ：SpringMVC用来转换Http请求和响应，对象----->json
  - `HttpMessageConverters` 是从容器中确定，获取所有的HttpMessageConverters
  - 自己给容器中添加HttpMessageConverters，只需要将组件注册在容器中
- Automatic registration of `MessageCodesResolver` (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/htmlsingle/#boot-features-spring-message-codes)).   定义错误代码生成规则
- Static `index.html` support.    静态首页访问
- Custom `Favicon` support (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-favicon)). 
- Automatic use of a `ConfigurableWebBindingInitializer` bean (covered [later in this document](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-web-binding-initializer)).
  - 我们可以配置一个ConfigurableWebBindingInitializer来替换默认的（添加到容器中）
  - 初始化WebDataBinder
  - 请求数据=====javaBean

If you want to keep those Spring Boot MVC customizations and make more [MVC customizations](https://docs.spring.io/spring/docs/5.2.6.RELEASE/spring-framework-reference/web.html#mvc) (interceptors, formatters, view controllers, and other features), you can add your own `@Configuration` class of type `WebMvcConfigurer` but **without** `@EnableWebMvc`.

If you want to provide custom instances of `RequestMappingHandlerMapping`, `RequestMappingHandlerAdapter`, or `ExceptionHandlerExceptionResolver`, and still keep the Spring Boot MVC customizations, you can declare a bean of type `WebMvcRegistrations` and use it to provide custom instances of those components.

If you want to take complete control of Spring MVC, you can add your own `@Configuration` annotated with `@EnableWebMvc`, or alternatively add your own `@Configuration`-annotated `DelegatingWebMvcConfiguration` as described in the Javadoc of `@EnableWebMvc`.



###### 2、扩展SpringMVC

**编写一个配置类（@Configuration），是WebMvcConfigurer类型，不能标注@EnableWebMvc**

既保留了所有的自动配置，也能用我们扩展的配置

```java
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //浏览器发送/test请求也来到成功页面
        registry.addViewController("/test").setViewName("success");
    }
}
```

原理：

​	1）、WebMvcAutoConfiguration是SpringMVC的自动配置类

​	2）、在做其他自动配置时会导入：@Import(EnableWebMvcConfiguration.class)

```java
@Configuration(proxyBeanMethods = false)
	public static class EnableWebMvcConfiguration extends DelegatingWebMvcConfiguration implements ResourceLoaderAware {
```

```java
//从容器中获取所有的WebMvcConfigurer
@Autowired(required = false)
	public void setConfigurers(List<WebMvcConfigurer> configurers) {
		if (!CollectionUtils.isEmpty(configurers)) {
			this.configurers.addWebMvcConfigurers(configurers);
            //将所有的WebMvcConfigurer相关配置都来一起调用
            //一个参考实现
            @Override
            public void addViewControllers(ViewControllerRegistry registry) {
                for (WebMvcConfigurer delegate : this.delegates) {
                    delegate.addViewControllers(registry);
                }
            }
		}
	}
```

​	3）、容器中所有的WebMvcConfigurer都会一起起作用

​	4）、自定义配置类也会被调用

效果：SpringMVC的自动配置和我们的扩展配置都会起作用

###### 3、全面接管SpringMVC

```java
@Configuration
@EnableWebMvc
public class MyMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //浏览器发送/test请求也来到成功页面
        registry.addViewController("/test").setViewName("success");
    }
}
```

SpringBoot对SpringMVC的自动配置不需要了，所有都自定义：所有的SpringMVC自动配置都失效了

**需要在配置类中添加@EnableWebMvc即可**

原理：

为什么@EnableWebMvc自动配置失效了

1）、@EnableWebMvc的核心

```java
@Import(DelegatingWebMvcConfiguration.class)
public @interface EnableWebMvc {
```

2）、

```java
@Configuration(proxyBeanMethods = false)
public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport {
```

3）、

```java
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })
//判断容器中没有这个组件的时候，这个自动配置类才生效
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@AutoConfigureAfter({ DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class,
		ValidationAutoConfiguration.class })
public class WebMvcAutoConfiguration {
```

4）、@EnableWebMvc将WebMvcConfigurationSupport导入进来了

5）、WebMvcConfigurationSupport只是SpringMVC最基本的功能

##### 五、如何修改SpringBoot的默认配置

模式：

​	1）、SpringBoot在自动配置很多组件的时候，先看容器中有没有用户自己配置的（@Bean、@Componet）如果有就用用户的的配置，如果没有，则自动配置，如果有些组件有多个，将用户配置的和默认的组合起来

​	2）、在SpringBoot中会有非常多的xxxxConfigurer帮我们进行扩展配置
### DAY 5

#### 一、安装linux虚拟机

1、VMWare，VirtualBox

2、新建虚拟机

3、双击运行虚拟机，使用root/123456

4、使用xshell等SSH工具连接

5、设置虚拟机网络

6、设置好网络之后使用命令重启虚拟机网络

```shell
#centOS命令
service network restart
```

7、查看linux的ip地址

```shell
ip addr
```

#### 二、在linux虚拟机上安装docker

步骤：

- 检查内核版本，必须是3.10及以上	
  - uname -r
- 安装docker
  - yum install docker
- 输入y确认安装
- 启动docker
  - [root@bogon ~]# systemctl start docker
    [root@bogon ~]# docker -v
    Docker version 1.13.1, build 64e9980/1.13.1
- 开机启动docker
  - [root@bogon ~]# systemctl enable docker
    Created symlink from /etc/systemd/system/multi-user.target.wants/docker.service to /usr/lib/systemd/system/docker.service.
- 停止docker
  - [root@bogon ~]# systemctl stop docker

#### 三、镜像操作

| 操作 | 命令                                             | 说明                                                   |
| ---- | ------------------------------------------------ | ------------------------------------------------------ |
| 检索 | docker search 关键字<br/>eg：docker search redis | 在docker hub上检索镜像的详细信息                       |
| 拉取 | docker pull 镜像名:tag                           | tag是可选的，tag表示标签，多为软件版本号，默认是latest |
| 列表 | docker images                                    | 查看所有本地镜像                                       |
| 删除 | docker rmi image-id                              | 删除指定的本地镜像                                     |

#### 四、容器操作

软件镜像（qq安装程序）----运行镜像------产生一个容器（正在运行的软件）

```shell
#1、搜索镜像
docker search tomcat
#2、拉取镜像
docker pull tomcat
#3、根据镜像启动容器
docker run --name mytomcat -d tomcat
#4、查看运行中的容器
docker ps
#5、停止运行中的容器
docker stop CONTAINER ID/mytomcat
#6、查看所有的容器
docker ps -a
#7、启动容器
docker start CONTAINER ID
#8、删除容器
docker rm CONTAINER ID
#9、启动一个做了端口映射的tomcat
docker run --name mytomcat -d -p 8080:8080 tomcat
-d:后台运行
-p:将主机端口映射到容器的一个端口  主机端口:容器内部的端口
#10、配置端口号
firewall-cmd --zone=public --add-port=5672/tcp --permanent   # 开放5672端口
firewall-cmd --zone=public --remove-port=5672/tcp --permanent  #关闭5672端口
firewall-cmd --reload   # 配置立即生效
firewall-cmd --zone=public --list-ports	#查看防火墙所有开放的端口
systemctl stop firewalld.service	#关闭防火墙
firewall-cmd --state	#查看防火墙状态
#11、进入容器
docker exec -it CONTAINER ID /bin/bash
#12、查看容器日志
docker logs CONTAINER ID
```

mysql启动需设置密码

```shell
docker run -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=123456 -d mysql
```
### DAY 6

#### 一、JDBC

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

```yaml
spring:
  datasource:
    url: jdbc:mysql://192.168.0.130:3306/dennis_jdbc
    driver-class-name: com.mysql.jdbc.Driver
    username: root
    password: 123456
```

效果：

​	默认使用class com.zaxxer.hikari.HikariDataSource作为数据源

​	数据源的相关配置都在DataSourceProperties中

自动配置原理：

org.springframework.boot.autoconfigure.jdbc

##### 1、参考DataSourceConfiguration，根据配置创建数据源，默认使用Hikari；可以使用spring.datasource.type指定自定义的数据源类型

##### 2、SpringBoot默认支持：

```java
org.apache.tomcat.jdbc.pool.DataSource
com.zaxxer.hikari.HikariDataSource
org.apache.commons.dbcp2.BasicDataSource
```

##### 3、自定义数据源类型

```java
/**
 * Generic DataSource configuration.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(DataSource.class)
@ConditionalOnProperty(name = "spring.datasource.type")
static class Generic {
	@Bean
	DataSource dataSource(DataSourceProperties properties) {
        //使用DataSourceBuilder创建数据源，利用反射创建响应type的数据源，并且绑定相关属性
		return properties.initializeDataSourceBuilder().build();
	}
}
```

##### 4、DataSourceInitializerInvoker实现了ApplicationListener

​	作用：

1. ```java
   private void initialize(DataSourceInitializer initializer) {
   	try {
   		this.applicationContext.publishEvent(new DataSourceSchemaCreatedEvent(initializer.getDataSource()));
   		// The listener might not be registered yet, so don't rely on it.
   		if (!this.initialized) {
   			this.dataSourceInitializer.initSchema();
   			this.initialized = true;
   		}
   	}
   	catch (IllegalStateException ex) {
   		logger.warn(LogMessage.format("Could not send event to complete DataSource initialization (%s)",
   				ex.getMessage()));
   	}
   }
   /**
    * Initialize the schema if necessary.
    * @see DataSourceProperties#getData()
    */
   void initSchema() {
   	List<Resource> scripts = getScripts("spring.datasource.data", this.properties.getData(), "data");
   	if (!scripts.isEmpty()) {
   		if (!isEnabled()) {
   			logger.debug("Initialization disabled (not running data scripts)");
   			return;
   		}
   		String username = this.properties.getDataUsername();
   		String password = this.properties.getDataPassword();
   		runScripts(scripts, username, password);
   	}
   }
   ```

   运行建表语句

2. ```java
   @Override
   public void onApplicationEvent(DataSourceSchemaCreatedEvent event) {
   	// NOTE the event can happen more than once and
   	// the event datasource is not used here
   	DataSourceInitializer initializer = getDataSourceInitializer();
   	if (!this.initialized && initializer != null) {
   		initializer.initSchema();
   		this.initialized = true;
   	}
   }
   ```

   运行插入数据的语句

默认只需要讲文件命名为：

```properties
schema-*.sql, data-*.sql
```

```yaml
initialization-mode: always
#可以使用下面的属性来指定位置
schema:
	- classpath:department.sql
```

##### 5、操作数据：自动配置了JdbcTemplateAutoConfiguration操作数据库

#### 二、整合MyBatis

```xml
 <dependency>
     <groupId>org.mybatis.spring.boot</groupId>
     <artifactId>mybatis-spring-boot-starter</artifactId>
     <version>2.1.2</version>
 </dependency>
```

##### 1、注解版：

```java
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
```

```yaml
mybatis:
  configuration:
    map-underscore-to-camel-case: true	#开启驼峰命名规则
  mapper-locations: classpath:mybatis.mapper/*.xml	#指定sql映射文件的位置
```

```java
//使用@MapperScan批量扫描所有的mapper接口
@MapperScan(value = "com.dennis.springboot.mapper")
```

#### 三、整合JPA

##### 1、整合SpringData JPA

JPA:ORM(Object Relational Mapping)

1. 编写一个实体类（bean）来和数据表进行映射，并且配置好映射关系

   ```java
   @Entity
   @Data
   @Table(name = "tbl_user")
   public class User {
   
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
   
       @Column(name = "last_name")
       private String lastName;
   
       @Column
       private String email;
   }
   ```

2. 编写Dao接口来操作实体类对应的数据表（Repository）

   ```java
   public interface UserRepository extends JpaRepository<User,Long> {
   }
   ```

3. 基本的配置

   ```yaml
   spring:
       jpa:
           hibernate:
             ddl-auto: update  #更新/创建数据表
           show-sql: true  #控制台打印sql
   ```

#### 四、Spring缓存抽象

##### 1、概念&缓存注释

| Cache          | 缓存接口，定义缓存操作，实现有：Redis、EhCache、ConcurrentMapCache等 |
| -------------- | ------------------------------------------------------------ |
| CacheManager   | 缓存管理器，管理各种缓存组件                                 |
| @Cacheable     | 主要针对方法配置，能够根据方法的请求参数对其结果进行缓存     |
| @CacheEvict    | 清空缓存                                                     |
| @CachePut      | 保证方法被调用，又希望结果被缓存                             |
| @EnableCaching | 开启基于注解的缓存                                           |
| KeyGenerator   | 缓存数据时Key生成策略                                        |
| serialize      | 缓存数据时value序列化策略                                    |

##### 2、快速体验

1. 开启基于注解的缓存

2. 标注缓存注解：

   - **@Cacheable**：将方法的运行结果进行缓存，以后再要相同的数据时，对缓存的真正CRUD操作再cache组件中，每一个缓存组件有自己的名字

     1）、cacheNames/value：指定缓存组件的名字

     2）、key：缓存数据使用的Key，可以用它来指定，默认使用方法参数的值。

     ​					编写SpEL：#id;参数id的值	#a0	#p0	#root.args[0]

     3）、keyGenerator：key的生成器，可以自定义。跟key属性二选一使用

     4）、cacheManager/cacheResolver：指定缓存管理器；cacheResolver指定缓存解析器（二选一）

     5）、condition：指定符合条件的情况下才缓存

     6）、unless：否定缓存，当unless指定的条件为true，方法的返回值就不会被缓存，可以获取到结果进行判断

     7）、sync：是否使用异步模式

   - **@CachePut**：既调用方法，又更新缓存，修改数据之后，同时更新缓存。

     运行时机：

     1）、先调用目标方法

     2）、将目标方法的结果缓存起来

     ```java
     @CachePut(value = "emp", key = "#result.id")
     @Override
     public Employee updateEmp(Employee employee) {
         employeeMapper.updateEmp(employee);
         return employee;
     }
     ```

   - @CacheEvict：删除缓存

     1）、allEntries = true：指定清除这个缓存中的所有数据

     2）、beforeInvocation = false：删除缓存是否在方法之后执行，默认代表是在方法执行之后执行，如果出现异常，缓存就不会清除

   - @Caching：定义复杂的缓存注解

   - @CacheConfig：抽取缓存的公共配置

#### 五、整合redis

##### 1、步骤：

1、安装redis，使用docker

2、引入redis的starter

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

3、配置redis

```yaml
spring:
	redis:
  		host: 192.168.0.130
```

使用StringRedisTemplate/RedisTemplate操作缓存

```java
@Test
void testRedis() {
    Employee employee = employeeMapper.getEmpById((long) 2);
    //默认如果保存对象，使用jdk序列化机制，序列化后的数据保存到redis
    //1、将数据以json方式保存
    //1）自己将对象转为json
    //2）redisTemplate默认序列化规则,改变默认的序列化规则
    empRedisTemplate.opsForValue().set("com:dennis:test02", employee);
}
```

2、测试缓存

原理：

​	1）、引入redis的starter，容器中保存的是RedisCacheManager

​	2）、RedisCacheManager创建RedisCache来作为缓存组件，RedisCache通过操作redis操作缓存

​	3）、默认保存数据k-v都是Object，利用序列化保存，如何保存JSON

- 引入redis的starter，cacheManager变为RedisCacheManager
- 默认创建的RedisCacheManager操作redis的时候使用的是RedisTemplate<Object, Object>
- RedisTemplate<Object, Object>是默认使用jdk序列化机制

#### 六、JMS&AMQP

**JMS（Java Message Service）JAVA消息服务**

——基于JVM消息代理的规范。ActiveMQ、HornetMQ是JMS实现

**AMQP（Advanced Message Queuing Protocol）**

——高级消息队列协议，也是一个消息代理的规范，兼容JMS

——RabbitMQ是AMQP的实现

##### 1、整合RabbitMQ

步骤：

1. ​	引入依赖

   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-amqp</artifactId>
   </dependency>
   ```

   - RabbitAutoConfiguration
   - 自动配置了连接工厂CachingConnectionFactory
   - RabbitProperties封装了 RabbitMQ的配置
   - RabbitTemplate：给RabbitMQ发送和接收消息
   - AmqpAdmin：RabbitMQ系统管理功能组件