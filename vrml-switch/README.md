[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-switch

对开关功能进行了封装，便捷的根据开关配置执行不同的Runnable，避免了大量if-else的开关逻辑

## Usage

### Maven

```xml

<dependency>
    <groupId>com.kevinten</groupId>
    <artifactId>vrml-switch</artifactId>
    <version>1.0.0</version>
</dependency>
```

### SwitchesAPI

Switches API提供了基于开关配置执行Runnable的一系列方法。

```java
// 如果开关打开，则执行Runnable操作
Switches.INS.runWithSwitch("test",()->System.out.println("switches is true"));

// 如果开关打开，则执行Callable操作，并返回call值
String call=Switches.INS.callWithSwitch("test",()->s);

// 如果开关打开，则执行Callable操作，并返回call值；否则，返回默认值
String call=Switches.INS.callWithSwitchOrDefault("false",()->s1,s2);
``` 

## Example

```java
// 如果开关打开，则执行Runnable操作
Switches.INS.runWithSwitch("test",()->System.out.println("switches is true"));

// 如果开关打开，则执行Callable操作，并返回call值
String call=Switches.INS.callWithSwitch("test",()->s);

// 如果开关打开，则执行Callable操作，并返回call值；否则，返回默认值
String call=Switches.INS.callWithSwitchOrDefault("false",()->s1,s2);
``` 

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-switch).

### Maven

```xml

<dependency>
    <groupId>com.kevinten</groupId>
    <artifactId>vrml-switch</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.