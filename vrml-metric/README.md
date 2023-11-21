[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-metric

A API to record application's metrics data for [vrml](https://github.com/kevinten10/vrml) library

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-metric</artifactId>
  <version>2.0.0</version>
</dependency>
```

### Metric Design

#### Design goals

* Metric code and main process isolation
* Support dynamic configuration of multiple levels
* Provide easy-to-use API tools

#### How to do it?

* Isolation of each exception
* Support dynamic configuration of multiple levels
* Support shard embedding metric point based on `ThreadLocal`
* Support complex expression evaluation

### Metric API

The Metric API provides a series of methods to record application's metrics data.

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
  <version>2.0.0</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.