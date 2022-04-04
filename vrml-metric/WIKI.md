# Metric 埋点API的设计

用于记录应用程序埋点数据的API

### 埋点API的问题

1. 主流程隔离

    例如旁支逻辑的NPE或者GSON解析异常等
    
2. 支持Debug级别

3. 简洁易用的API

### 实现思路和方案

1. 单个异常的隔离

    通过闭包进行trycatch，进行异常的隔离

2. 支持不同级别的配置

    通过闭包传入配置，实现动态的配置
    
3. 简洁易用的API

### Metrics API

* 单个异常的隔离 √
* 支持不同级别的配置 √
* 简洁易用的API √
* 支持局部分片 √
* 支持表达式 √

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

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-metric</artifactId>
  <version>1.0.3</version>
</dependency>
```