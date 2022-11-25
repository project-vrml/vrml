[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-trace

基于MDC功能进行日志Tag追踪的API

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-trace</artifactId>
  <version>1.1.1</version>
</dependency>
```

### Mdc 日志Tag追踪

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

### Map 上下文对象传递数值

TraceAPI可以使用`MapTraces`管理上下文对象，能够在线程任意一处获取该上下文对象，从而在上下文对象的Map中传递追踪数值。

```java
  // 构造实现{@code Traceable}的上下文对象 
  TestTraceable testTraceable = new TestTraceable();

  // 将上下文对象初始化到threadlocal
  MapTraces.useThreadLocal().initObj(testTraceable);

  // 将(key,value)放入threadlocal中的上下文对象中
  MapTraces.useThreadLocal().trace("test", "test");

  // 将多个(key,value)值累加的放入threadLocal上下文对象
  MapTraces.useThreadLocal().traceAdd("test1", "test2");

  // 清除threadLocal上下文对象
  MapTraces.useThreadLocal().clear();
```

### Map Mdc 包含MDC和上下文对象两种方式的追踪API

包含MDC的日志Tag追踪功能 和 上下文对象传递数值功能

```java
  // you can initialize some initial values
  MapMdcTraces.useThreadLocal().initMdc("key","value");
  
  // you can put some values to MDC temporarily
  MapMdcTraces.useThreadLocal().put("key", "value");

  // remove the key from threadLocal mdc values
  MapMdcTraces.useThreadLocal().remove("key");

  // clear all threadLocal mdc values
  MapMdcTraces.useThreadLocal().clearMdc();
```

```java
  // construct your context object which implements {@code Traceable}
  TestTraceable testTraceable = new TestTraceable();

  // you can initizlize your context object to threadloacl
  MapMdcTraces.useThreadLocal().initObj(testTraceable);

  // put the (key,value) to your threadLocal context object
  MapMdcTraces.useThreadLocal().trace("test", "test");

  // put the addition value of the key to your threadLocal context object
  MapMdcTraces.useThreadLocal().traceAdd("test2", "test2");

  // clear threadLocal context object
  MapMdcTraces.useThreadLocal().clear();
```

## Example

### 使用MDC TraceAPI记录日志的Tag

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

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-trace).

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-trace</artifactId>
  <version>1.1.1</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.