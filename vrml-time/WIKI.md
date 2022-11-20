# Time 时间API的设计

基于ThreadLocal的高性能时间处理API，同时提供时区解析和CRON表达式解析功能

### 问题背景

在项目中，关于时间处理一直是比较混乱的一部分。

JDK提供的原生的时间处理API虽然强大，但并非简单易用，同时还有并发时的各种问题。

### 解决思路

通过ThreadLocal解决JDK的并发问题，避免了不断地new对象造成的性能损耗。

同时结合实际开发的情况，重点关注三种时间格式之间的转换（`Date/Time/DateTime`），

提供了三种对象结构（`Date/Timestamp/String`）之间的各种转换方式。
 
### 用例演示

基于ThreadLocal的时间工具，避免重复创建DateFormat的开销。

重点关注三种时间格式：`Date/Time/DateTime`

主要操作三种对象结构：`Date/Timestamp/String`

```java
  // use static func
  ThreadLocalTimeUtils.func(params);
```

#### 时区解析API

时区转换.

TimeZone -12 ~ +12 based on UTC.

```java
  // TimeZone conversion
  TimeZoneUtils.parseTimezoneTimestamp(sourceTimezone, targetTimezone, sourceTimestamp);
```

#### Cron表达式解析API

解析Cron表达式.

支持三种Cron类型 : DAY/WEEK/MONTH.

同时支持时区Cron的转换.

```java
  // cron expression resolution for timezone conversion
  CronExpressionUtils.parseCronToTargetTimeZone(TimeZoneCronParseBuilder builder);
  
  // cron expression resolution
  CronExpressionUtils.parseCron(SimpleCronParseBuilder builder);
```

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-time</artifactId>
  <version>1.1.0</version>
</dependency>
```