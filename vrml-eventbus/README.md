[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-eventbus

A publish event API for [vrml](https://github.com/kevinten10/vrml) library

## Usage

### Maven

```xml

<dependency>
    <groupId>group.rxcloud</groupId>
    <artifactId>vrml-eventbus</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Publish Event To Listener

基于Spring的事件发布监听机制进行的增强API。

支持同步和异步两种模式。

异步模式有以下特性：
+ Event的clone
+ 异步线程池进行Listener处理

#### Define Event

继承`event`包中的抽象类，定义要传递的Event对象。

```java
/**
 * The process context complete event.
 */
public class MyEvent extends AbstractProcessEvent<AbstractContext, MyEvent> {

    /**
     * Create a new Abstract Event.
     *
     * @param source   the object on which the event initially occurred (never {@code null})
     * @param context  the request
     * @param response the response
     */
    public MyEvent(Object source, AbstractContext context, Response response) {
        super(source, context, response);
    }
}
```

#### Listen Event

继承`listener`包中的抽象类，定义Event的Listener对象。

使用`@EventListener`进行Event的监听。

```java
public class MyEventListener extends AbstractEventListener {

    @EventListener
    public void onEvent(MyEvent stepEvent) {
        
    }
}
```

#### Publish Event

继承`publisher`包中的抽象类，定义Event的Publisher对象。

```java
@Component
public class MyEventPublisher extends AbstractSyncEventPublisher<AbstractMyEvent> {

}
```

## Example

### Unit test

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-eventbus).

### Maven

```xml

<dependency>
    <groupId>group.rxcloud</groupId>
    <artifactId>vrml-eventbus</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.