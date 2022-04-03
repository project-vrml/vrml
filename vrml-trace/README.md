[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-trace

A tracing Api base on MDC for [vrml](https://github.com/kevinten10/vrml) library

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-trace</artifactId>
  <version>1.0.2</version>
</dependency>
```

### Mdc Traces

Use MDC for tracing

```java
  // you can initialize some initial values
  MdcTraces.useThreadLocal().initMdc("key","value");
  
  // you can put some values to MDC temporarily
  MdcTraces.useThreadLocal().put("key", "value");

  // remove the key from threadLocal mdc values
  MdcTraces.useThreadLocal().remove("key");

  // clear all threadLocal mdc values
  MdcTraces.useThreadLocal().clearMdc();
```

### Map Traces

Use obj.map for tracing

```java
  // construct your context object which implements {@code Traceable}
  TestTraceable testTraceable = new TestTraceable();

  // you can initizlize your context object to threadloacl
  MapTraces.useThreadLocal().initObj(testTraceable);

  // put the (key,value) to your threadLocal context object
  MapTraces.useThreadLocal().trace("test", "test");

  // put the addition value of the key to your threadLocal context object
  MapTraces.useThreadLocal().traceAdd("test2", "test2");

  // clear threadLocal context object
  MapTraces.useThreadLocal().clear();
```

### Map Mdc Traces

Both Mdc and Map Traces.

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

### Use Mdc Traces as log tag

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

Add the MDC tag item **%X{messageId}%X{local}** to the log file.

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
  <version>1.0.2</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.