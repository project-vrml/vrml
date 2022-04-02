[![vrml-logo](./resources/logo.png)](./README.md)

Vrml是基于Java 8的常用拓展工具集合，旨在提供更健壮和更方便易用的常用工具，以解决工作中常见的问题。

它提供了监控埋点，日志组件，网络请求代理，错误码设计规范，告警和事件监听等等。

Vrml对这些工作中常见的场景中可能遇到的问题给出了优雅和强大的解决方案。

使用环境主要同Spring集成使用，基于Spring实现接口动态发现的机制，但也提供非Spring下的自定义配置功能。

Vrml主要基于Vavr库（以及其他常用库）进行构建，编码参考了Vavr的函数式风格。

由于Vrml提供了模块化的机制，您可以方便的引用所需要的功能到您的类路径中，或者使用-all引入所有。

要保持最新状态，请关注[blog](./README.md) 。

## Using Vrml

See [User Guide](./README.md).

-------------------------------------------------------------------------------

## Modules

### [vrml-alert](./vrml-alert) 

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-alert/WIKI.md)

An alert API that supports multiple methods for [vrml](./README.md) library

Alerts支持各种快捷灵活方式的告警API，避免过程式的调用告警service

---------------------------------------------------------------------

### [vrml-api](./vrml-api)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-api/WIKI.md)

A Aspect to log request process for [vrml](./README.md) library

---------------------------------------------------------------------

### [vrml-compute](./vrml-compute)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-compute/WIKI.md)

对统计触发的行为进行了封装，可以基于统计指标执行不同的Runnable操作。

---------------------------------------------------------------------

### [vrml-data](./vrml-data)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

A common data structure extension API for [vrml](./README.md) library

---------------------------------------------------------------------

### [vrml-error](./vrml-error)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-error/WIKI.md)

A set of error code definition specifications for [vrml](./README.md) library

---------------------------------------------------------------------

### [vrml-eventbus](./vrml-eventbus)

TODO

---------------------------------------------------------------------

### [vrml-external](./vrml-external)

Other auxiliary tools library

---------------------------------------------------------------------

### [vrml-log](./vrml-log)

A API to log by the key for [vrml](./README.md) library

[WIKI](./vrml-log/WIKI.md)

---------------------------------------------------------------------

### [vrml-metric](./vrml-metric)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-metric/WIKI.md)

A API to record application's metrics data for [vrml](./README.md) library

---------------------------------------------------------------------

### [vrml-request](./vrml-request)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-request/WIKI.md)

A proxy API to wrap remote request with log/record/check/... for [vrml](./README.md) library

---------------------------------------------------------------------

### [vrml-switch](./vrml-switch)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

Different Runnable can be executed conveniently according to the switches configuration, avoiding a lot of if-else switches logic.

---------------------------------------------------------------------

### [vrml-time](./vrml-time)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-time/WIKI.md)

A time API with timezone/cron for [vrml](./README.md) library

---------------------------------------------------------------------

### [vrml-trace](./vrml-trace)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-trace/WIKI.md)

A api of MDC/MAP traces for [vrml](./README.md) library

-------------------------------------------------------------------------------

### Maven

You can import all vrml modules:

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-all</artifactId>
  <version>1.0.1</version>
</dependency>
```

Latest feature branch:

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-all</artifactId>
  <version>1.0.1</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our [contribution guide](./CONTRIBUTING.md) for details.
