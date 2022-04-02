[![vrml-logo](./resources/logo.png)](./README.md)

VRML is a set of common extension tools based on Java 8, designed to provide more robust and easy to use common tools to solve common problems in the workplace.

It provides monitoring metrics, logging components, network request agents, error code design specifications, alarms and event listening, and more.

VRML provides elegant and powerful solutions to the problems you might encounter in a common scenario in these jobs.

The usage environment is mainly integrated with `Spring`, based on Spring to implement the mechanism of dynamic interface discovery, but also provides non-Spring custom configuration functions.

VRML is built primarily on the `VAVR` library (and other commonly used libraries), and the coding references the functional style of VAVR.

Because VRML provides a modular mechanism, you can easily refer to the functionality you need in your classpath, or import all using -all.

To stay up to date, Please follow the [blog](./README.md).

## Using Vrml

See [User Guide](./README.md).

查看 [使用文档](./README_CN.md).

-------------------------------------------------------------------------------

## Modules

### [vrml-alert](./vrml-alert) 

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-alert/WIKI.md)

An alert API that supports multiple methods for [vrml](./README.md) library

---------------------------------------------------------------------

### [vrml-api](./vrml-api)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-api/WIKI.md)

A Aspect to log request process for [vrml](./README.md) library

---------------------------------------------------------------------

### [vrml-compute](./vrml-compute)

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-compute/WIKI.md)

The adaptor of statistical triggering, and different Runnable operations can be performed based on statistical indicators.

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

[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

[WIKI](./vrml-eventbus/README.md)

A publish event API for [vrml](https://github.com/kevinten10/vrml) library

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