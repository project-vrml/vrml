[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-metric

用于记录应用程序埋点数据的API

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-metric</artifactId>
  <version>1.0.4</version>
</dependency>
```

### Metric Design

#### 设计目标

* 埋点代码和主流程隔离
* 支持多级动态配置
* 提供易于使用的API工具

#### How to do it?

* 隔离每个异常
* 支持多级动态配置
* 支持基于`ThreadLocal`的分片买点
* 支持复杂的条件表达式

### Metric API

Metric API提供了一系列方法来记录应用程序的埋点数据。

```java
/**
 * API demo show
 */
private static void metrics(BiConsumer<Map<String, String>, Map<String, String>> howToShow) {
    //  Option 1: add metric manually
    Metrics.metric(() -> {
        // index
        index(MetricIndexs.metric_type, "index");
        // store
        store(MetricStores.context, "store");
        // exception
        exception(new RuntimeException());
        // object
        object(new Object());
    });
    howToShow.accept(Metrics.showIndexs(), Metrics.showStores());
    Metrics.build();
    // Option 2: add metric by automatic placeholder "$"
    Metrics.Log().of(
            // index
            $(MetricIndexs.metric_type, "index"),
            // store
            $(MetricStores.context, "store"),
            // exception
            $(new RuntimeException()),
            // object
            $(new Object())
    );
    howToShow.accept(Metrics.showIndexs(), Metrics.showStores());
    Metrics.build();
    // Also you can use {@code local()} to start a local scope
    String local = Metrics.local();
    Metrics.Log(local).of(
            // Also you can use supplier
            () -> $(MetricIndexs.metric_type, "index"),
            () -> $(MetricStores.context, "store"),
            () -> $(new RuntimeException()),
            () -> $(new Object())
    );
    howToShow.accept(Metrics.showIndexs(), Metrics.showStores());
    Metrics.build(local);
}
```

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-request).

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-metrics</artifactId>
  <version>1.0.4</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.