# Log 日志API的设计

一个支持动态配置的日志记录API

### 日志的问题

很难从info和warn里找到有效信息

配置基于全局，很难进行动态调整

#### Design goals

* 支持动态配置打印不同级别的日志
* 支持方便的添加日志TAG
* 支持对不同的日志基于TAG进行分类

#### How to do it?

* 支持多级动态配置
* 提供易于使用的API以操作TAG
* 日志对象将TAG保留为成员变量

### Log specification

①Tags ②基础信息 ③具体日志内容
  
[[ <-- tag --> ]][ <- basic info -> ] [ content ]

[[messageId,key]][ClassName.FuncName] log content.

### Log convention 
  
1. 动态参数小于3，并列一行
2. 动态参数小于3，但表达式较长，参数作为单独一行
3. 动态参数大于等于3，参数作为单独一行

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-log</artifactId>
  <version>1.1.0</version>
</dependency>
```