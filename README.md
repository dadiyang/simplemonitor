# 简单监控模块

一个面向切面的实践项目，方法级别的简单监控

## 背景

在开发过程中，我们经常会需要对方法进行一些简单的监控，例如监控某个方法的执行时间，必要的时候打印入参和返回值，对抛出的异常进行监控。

这样的一些监控点虽然很小，但是散落在各处而且侵入到业务逻辑当中让业务代码显得非常杂乱。

这些点让开发人员将关注点从自己的业务上移开，让他们关注了与当前功能无关的事情，增加了开发成本。

因此，将这个切面抽离出来变得非常有意义，所以有了本项目

## 原理

使用SpringAOP，对加了指定注解的方法或加了指定注解的类的所有方法进行环切（@Around），记录执行时间，并通过连接点（ProceedingJoinPoint）获取所需的信息

## 使用

在maven中引用

### 一、引用依赖(目前没有发布到中央仓库，所以需要先 mvn install)

```xml
<dependency>
    <groupId>com.github.dadiyang</groupId>
    <artifactId>simplemonitor</artifactId>
    <version>1.0.0</version>
</dependency>
```
### 二、向Spring容器中注册 `TimeConsumingMonitor` Bean 

有三种方式可以向Spring容器注册Bean

* 使用Spring的ComponentScan可以将 `com.github.dadiyang.simplemonitor` 添加到 **basePackages** 中：`@ComponentScan(basePackages = {com.github.dadiyang.simplemonitor})`

* 如果使用SpringBoot，可以设置SpringBootApplication注解的scanBasePackages属性：`@SpringBootApplication(scanBasePackages ={"com.github.dadiyang.simplemonitor"})`

* 或者直接使用@Bean注解注册

```java
@Bean
public TimeConsumingMonitor timeConsumingMonitor() {
    return new TimeConsumingMonitor();
}
```

### 三、添加@EnableAspectJAutoProxy

在Spring的配置类上添加 `@EnableAspectJAutoProxy` 注解(SpringBoot会自动打开AOP的支持，所以可以不添加此注解)

### 四、 向需要监控的方法或类上打 `@TimeConsuming` 注解


如果只需要监控某个方法，可以将这个注解打在方法上。如果是想监控某个类的所有方法，则将这个注解打到类上

## 输出样例

```
INFO com.github.dadiyang.simplemonitor.TestMethodBaseMonitor - I'm method annotated, a:run run run！, b:here we go
INFO com.github.dadiyang.simplemonitor.TimeConsumingMonitor - 调用方法TestMethodBaseMonitor.testTimeConsuming(..), 参数: ["run run run！","here we go"], 结果: "OK2", 执行耗时: 16
INFO com.github.dadiyang.simplemonitor.TestMethodBaseMonitor - debug level and useSourceClassLog:debug test, b:sourceClass
DEBUG com.github.dadiyang.simplemonitor.TestMethodBaseMonitor - 调用方法TestMethodBaseMonitor.testDebugLevel(..), 参数: ["debug test","sourceClass"], 结果: "haha", 执行耗时: 1
```

## 参数配置

通常情况下，默认的配置是足够使用了，但是如果你有更加个性化的需求，可以对以下同几个参数进行配置

* **fullMsg** 是否打印完整的方法名、方法参数和返回值，默认 **false**

* **fullArg** 是否打印完整的参数，默认 **false**

* **fullReturnVal** 是否打印完整的返回值，默认 **false**

* **fullMethodName** 是否打印完整的方法名称（包括全类名和方法的全类名），默认 **false**

* **monitorException** 是否监控方法抛出的异常，默认 **true**  
  **注意**：监控方法抛出的异常只会以logLevel指定的日志级别打印方法相关信息和 e.getMessage()，最后把异常重新抛出
  
* **logLevel** 日志级别(0: TRACE, 1: DEBUG, 2: INFO, 3: WARN, 4: ERROR)，默认 2，即INFO级别

* **useSourceClassLog** 是否使用被注解方法所属的类对应的日志类进行日志输出  
  即 `LoggerFactory.getLogger`(方法所属类);
