[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-data

A common data structure extension API for [vrml](https://github.com/kevinten10/vrml) library

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-data</artifactId>
  <version>1.1.1</version>
</dependency>
```

### Ability

You can impl those abilities to your data type.

#### Holdable

Use Holdable to cache your lazy load data, then calculate and get it at the first time.

```java
  // define the class impl Holdable
  private static class TestHoldable implements Holdable {
      private Holder<Object> holder;
  }

  TestHoldable testHoldable = new TestHoldable();
  
  // put a lazy load data in Holder
  testHoldable.holder = new Holdable.Holder<>(() -> "str");

  // get the lazy load data from Holder
  testHoldable.holder.getData();
```

#### Traceable

The data type can impl this interface, and then it can hold a trace map to tracing some data.

### Result

函数大致分为四种类型：

1. 逻辑判断型
2. 操作型
3. 获取数据型
4. 数值计算/处理型

EX:(返回函数的函数)

The return results of function operations are of the following types: operational/logical/...

This package defines the common results class for those operations.

```java
  // logical operation result

  LogicalResult.success(); // success
  LogicalResult.failure(); // failure
  LogicalResult.unknown(); // unknown
``` 

```java
  // process operation result

  ProcessResult.success(); // success
  ProcessResult.success("obj"); // success with success result obj

  ProcessResult.failure("str"); // failure with error message
  ProcessResult.failure("str", "obj"); // failure with error message and failure result obj

  ProcessResult.unknown("str"); // unknown with error message
  ProcessResult.unknown("str", "obj"); // unknown with error message and unknown result obj
``` 

## Example

### Unit test

Check it out below:

```java
import HoldableTest;
import LogicalResultTest;
import ProcessResultTest;
``` 

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-data).

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-data</artifactId>
  <version>1.1.1</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.