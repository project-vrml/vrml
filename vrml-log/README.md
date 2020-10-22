[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-log

A API to log by the key for [vrml](https://github.com/kevinten10/vrml) library

## Usage

### Maven

```xml
<dependency>
  <groupId>com.kevinten</groupId>
  <artifactId>vrml-log</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Log Design

#### Design goals

* 支持动态配置打印不同级别的日志
* 支持方便的添加日志TAG
* 支持对不同的日志基于TAG进行分类

#### How to do it?

* Support dynamic configuration of multiple levels
* Provide easy-to-use API to operate TAG
* Log Object holds TAG as a member variable

### Log specification

①Tags ②基础信息 ③具体日志内容
  
[[ <-- tag --> ]][ <- basic info -> ] [ content ]

[[messageId,key]][ClassName.FuncName] log content.

### Log convention 
  
1. 动态参数小于3，并列一行
2. 动态参数小于3，但表达式较长，参数作为单独一行
3. 动态参数大于等于3，参数作为单独一行

### Log API

```java
```

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-request).

### Maven

```xml
<dependency>
  <groupId>com.kevinten</groupId>
  <artifactId>vrml-logs</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.