[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-time

基于ThreadLocal的高性能时间处理API，同时提供时区解析和CRON表达式解析功能

## Usage

### Maven

```xml
<dependency>
  <groupId>com.kevinten</groupId>
  <artifactId>vrml-time</artifactId>
  <version>1.0.0</version>
</dependency>
```

### 时间处理API

基于ThreadLocal的时间工具，避免重复创建DateFormat的开销。

重点关注三种时间格式：`Date/Time/DateTime`

主要操作三种对象结构：`Date/Timestamp/String`

```java
  // use static func
  ThreadLocalTimeUtils.func(params);
```

### 时区解析API

时区转换.

TimeZone -12 ~ +12 based on UTC.

```java
  // TimeZone conversion
  TimeZoneUtils.parseTimezoneTimestamp(sourceTimezone, targetTimezone, sourceTimestamp);
```

### Cron表达式解析API

解析Cron表达式.

支持三种Cron类型 : DAY/WEEK/MONTH.

同时支持时区Cron的转换.

```java
  // cron expression resolution for timezone conversion
  CronExpressionUtils.parseCronToTargetTimeZone(TimeZoneCronParseBuilder builder);
  
  // cron expression resolution
  CronExpressionUtils.parseCron(SimpleCronParseBuilder builder);
```

## Example

### 单元测试

请参照以下代码:

```java
import CronExpressionUtilsTest;
import TimeZoneUtilsTest;
import ThreadLocalTimeUtilsTest;
``` 

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-time).

### Maven

```xml
<dependency>
  <groupId>com.kevinten</groupId>
  <artifactId>vrml-time</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.