[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-switch

对开关功能进行了封装，便捷的根据开关配置执行不同的Runnable，避免了大量if-else的开关逻辑。

为了更好的对开关配置进行管理，Switch API支持json格式的多级配置。

## Usage

### Maven

```xml

<dependency>
    <groupId>group.rxcloud</groupId>
    <artifactId>vrml-switch</artifactId>
    <version>1.0.9</version>
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

支持JSON结构的嵌套开关：

```java
Switches.SwitchKey switchKey = Switches.SwitchKeyBuilder.builder()
        .next("test1")
        .next("test2")
        .next("test3")
        .build();

// 如果开关打开，则执行Runnable操作
Switches.INS.runWithSwitch(switchKey,()->System.out.println("switches is true"));

// 如果开关打开，则执行Callable操作，并返回call值
String call=Switches.INS.callWithSwitch(switchKey,()->s);

// 如果开关打开，则执行Callable操作，并返回call值；否则，返回默认值
String call=Switches.INS.callWithSwitchOrDefault(switchKey,()->s1,s2);
```

JSON结构配置示例如下：

> 请注意，对应项可以为不存在；但若存在，值必须为布尔值。

```json
{
  "test1": {
    "test2": {
      "test3": true
    }
  }
}
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
    <groupId>group.rxcloud</groupId>
    <artifactId>vrml-switch</artifactId>
    <version>1.0.9</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.