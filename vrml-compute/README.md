[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-compute

对统计触发的行为进行了封装，可以基于统计指标执行不同的Runnable操作。

## Usage

### Maven

```xml

<dependency>
    <groupId>group.rxcloud</groupId>
    <artifactId>vrml-compute</artifactId>
    <version>1.0.3</version>
</dependency>
```

### ComputeAPI

Compute API提供了基于统计指标执行Runnable的一系列方法。

```java
Computes.TC.compute("test",
        j::getAndIncrement,
        ()->{
        throw new RuntimeException("false");
        });

// 需要提供配置项的实现类
@Component
public class TimeCounterComputeConfigurationImpl implements TimeCounterComputeConfiguration {
    // ......
}
``` 

## Example

### Computes.TC实现

基于(时间x+计数y)两个维度的计算器。

在x时间内触发次数达到y时，将会执行right计算逻辑；否则触发left计算逻辑。

#### 统计性日志告警：在10s内触发100次，则执行error逻辑，否则执行warn逻辑

```java
Computes.TC.compute("test",
        ()->log.warn(),
        ()->log.error());
``` 

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-compute).

### Maven

```xml

<dependency>
    <groupId>group.rxcloud</groupId>
    <artifactId>vrml-compute</artifactId>
    <version>1.0.3</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.