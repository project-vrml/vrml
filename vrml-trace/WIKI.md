# Trace 追踪API的设计

基于MDC功能进行日志Tag追踪的API

### 问题背景

在项目中，经常需要通过日志的Tag等方式，将日志进行串联追踪。

如果在代码中手动的去打印Tag值，会造成每打印一次Tag都需要一次编码，同时方法还需要持有Tag的值对象。

一种解决方案是直接使用MDC，通过线程上下文来传递Tag的值对象，同时避免了在代码中手工的打印Tag。

Trace模块对MDC的方式进行了简单的封装，相较于直接使用MDC，语义更加清晰。

### 解决思路

封装MDC的API，对外提供一层语义更加明确的访问接口
 
### 用例演示

TraceAPI可以使用`MdcTraces`管理MDC，从而基于MDC进行日志的Tag管理。

```java
  // 初始化MDC的值
  MdcTraces.useThreadLocal().initMdc("key","value");
  
  // 向MDC中增加数值
  MdcTraces.useThreadLocal().put("key", "value");

  // 从MDC中移除数值
  MdcTraces.useThreadLocal().remove("key");

  // 清空当前线程的MDC
  MdcTraces.useThreadLocal().clearMdc();
```

#### 举例：使用MDC TraceAPI记录日志的Tag

```java
  try {
      // initialize global log tag at beginning
      MdcTraces.useThreadLocal().initMdc("messageId", "messageId");

      {
          // put local log tag
          MdcTraces.useThreadLocal().put("local", "value");
          {
              // your logic
              yourLogic();
          }
          // remove local log tag
          MdcTraces.useThreadLocal().remove("local");
      }
  } finally {
      // clear the threadLocal log tags
      MdcTraces.useThreadLocal().clearMdc();
  }
```

如果有必要，记得在配置文件中配置Tag的打印格式，例如 **%X{messageId}%X{local}**。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <!-- 1. use slf4j log appender -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>[%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36}] - %X{messageId}%X{local}%msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>

</configuration>
```

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-trace</artifactId>
  <version>1.1.0</version>
</dependency>
```