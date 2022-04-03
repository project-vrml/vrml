[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-netty

Netty拓展工具集。

## Usage

### Maven

```xml

<dependency>
    <groupId>group.rxcloud</groupId>
    <artifactId>vrml-netty</artifactId>
    <version>1.0.2</version>
</dependency>
```

### DirectMemoryIndicator

Netty对外内存探测器。

#### 使用

直接引入`vrml-netty`依赖包即可，通过SpringFactory会自动注入相关Bean。

#### 日志开关

为了方便对外探测日志的即开即用，`vrml-netty`使用`vrml-log`库作为日志输出控制器。

请参考`vrml-log`的使用方式，定义key为`netty_direct_memory`的log配置进行输出。

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-netty).

### Maven

```xml

<dependency>
    <groupId>group.rxcloud</groupId>
    <artifactId>vrml-netty</artifactId>
    <version>1.0.2</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.