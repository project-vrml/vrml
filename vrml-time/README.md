[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-time

A time API with timezone/cron for [vrml](https://github.com/kevinten10/vrml) library

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-time</artifactId>
  <version>1.1.2</version>
</dependency>
```

### Time util

The ThreadLocal time utils. Avoid the overhead of creating DateFormat repeatedly. Focus on the three types of Date/Time/DateTime. Operation orientation to the Date/Timestamp/String.

```java
  // use static func
  ThreadLocalTimeUtils.func(params);
```

### TimeZone util

TimeZone time conversion.

TimeZone -12 ~ +12 based on UTC.

```java
  // TimeZone conversion
  TimeZoneUtils.parseTimezoneTimestamp(sourceTimezone, targetTimezone, sourceTimestamp);
```

### Cron util

Parse the cron expression.

Three parsing approaches are supported : DAY/WEEK/MONTH.

Support for parsing timezone transitions.

```java
  // cron expression resolution for timezone conversion
  CronExpressionUtils.parseCronToTargetTimeZone(TimeZoneCronParseBuilder builder);
  
  // cron expression resolution
  CronExpressionUtils.parseCron(SimpleCronParseBuilder builder);
```

## Example

### Unit test

Check it out below:

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
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-time</artifactId>
  <version>1.1.2</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.