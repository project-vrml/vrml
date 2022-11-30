[![vrml-logo](./resources/logo.png)](./README.md)

> 读作：[ver, mao]

Vrml是基于Java 8的常用拓展工具集合，旨在提供更健壮和更方便易用的常用工具，以解决工作中常见的问题。

它提供了监控埋点，日志组件，网络请求代理，错误码设计规范，告警和事件监听等等。

Vrml对这些工作中常见的场景中可能遇到的问题给出了优雅和强大的解决方案。

使用环境主要同Spring集成使用，基于Spring实现接口动态发现的机制，但也提供非Spring下的自定义配置功能。

Vrml主要基于Spring（以及其他常用库）进行构建，编码参考了Vavr的函数式风格。

由于Vrml提供了模块化的机制，您可以方便的引用所需要的功能到您的类路径中，或者使用-all引入所有。

要保持最新状态，请关注[blog](./README.md) 。

## Vrml API

Vrml设计的核心理念同 [cloud-runtimes](https://github.com/capa-cloud/cloud-runtimes-jvm) API设计理念相同。

即定义一套框架/平台/具体实现等无关的API，面向接口进行编程，从而获得更好的拓展性和跨平台特性。

与 cloud-runtimes-api 语言无关的设计思路相比，Vrml主要为JVM编程语言设计，所以在API设计上糅合了Java的特性和Spring的使用方式。

面向API编程的理念决定了：Vrml可以在不同平台，不同框架体系中进行集成，您可以享受到`write once, run anywhere`的特性。

### Vrml With Cloud-Runtimes API

请参考 [vrml-cloudruntimes](https://github.com/project-vrml/vrml-cloudruntimes)

将Vrml API同Cloud-Runtimes API进行结合:

+ 由Cloud-Runtimes config实现Vrml config的能力
+ 由Cloud-Runtimes rpc实现Vrml rpc的能力
+ ...

这样，您可以在任何 Cloud-Runtimes 支持的平台上，使用Vrml的能力，例如：

+ Capa
+ Layotto
+ Dapr
+ ...

### Vrml With Custom API

您可以直接使用具体平台/框架体系实现Vrml API。

这样，您就可以在特定平台/框架体系中使用Vrml。

-------------------------------------------------------------------------------

## Modules

### [vrml-alert](./vrml-alert)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-alert/WIKI.md)

Alerts支持各种快捷灵活方式的告警API，避免过程式的调用告警service

---------------------------------------------------------------------

### [vrml-api](./vrml-api)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-api/WIKI.md)

API模块提供一个通用的基准切面，以记录接口的请求响应日志，同时支持配置化，使得更加灵活

---------------------------------------------------------------------

### [vrml-compute](./vrml-compute)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

对统计触发的行为进行了封装，可以基于统计指标执行不同的Runnable操作。

---------------------------------------------------------------------

### [vrml-data](./vrml-data)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

拓展性的数据接口定义。

---------------------------------------------------------------------

### [vrml-error](./vrml-error)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-error/WIKI.md)

一组错误代码定义规范

---------------------------------------------------------------------

### [vrml-eventbus](./vrml-eventbus)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

基于Spring的事件发布监听机制进行的增强API。

---------------------------------------------------------------------

### [vrml-external](./vrml-external)

其他不常用的拓展功能。

---------------------------------------------------------------------

### [vrml-log](./vrml-log)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-log/WIKI.md)

一个支持动态配置的日志记录API。

---------------------------------------------------------------------

### [vrml-metric](./vrml-metric)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-metric/WIKI.md)

用于记录应用程序埋点数据的API

---------------------------------------------------------------------

### [vrml-netty](./vrml-netty)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

Netty拓展工具集。

---------------------------------------------------------------------

### [vrml-request](./vrml-request)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-request/WIKI.md)

一个具有代理功能的API，用于对远程请求进行包装，从而进行日志记录/数值记录/响应检查/...

---------------------------------------------------------------------

### [vrml-resource](./vrml-resource)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

Java File工具集。

---------------------------------------------------------------------

### [vrml-spi](./vrml-spi)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

Java SPI工具集。

---------------------------------------------------------------------

### [vrml-switch](./vrml-switch)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-switch/WIKI.md)

对开关功能进行了封装，便捷的根据开关配置执行不同的Runnable，避免了大量if-else的开关逻辑。

---------------------------------------------------------------------

### [vrml-time](./vrml-time)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-time/WIKI.md)

基于ThreadLocal的高性能时间处理API，同时提供时区解析和CRON表达式解析功能。

---------------------------------------------------------------------

### [vrml-trace](./vrml-trace)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-trace/WIKI.md)

基于MDC功能进行日志Tag追踪的API。

-------------------------------------------------------------------------------

### Maven

You can import all vrml modules:

```xml

<dependency>
    <groupId>group.rxcloud</groupId>
    <artifactId>vrml</artifactId>
    <version>1.1.2</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

Latest feature branch:

```xml

<dependency>
    <groupId>group.rxcloud</groupId>
    <artifactId>vrml</artifactId>
    <version>1.1.2</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our [contribution guide](./CONTRIBUTING.md) for
details.
