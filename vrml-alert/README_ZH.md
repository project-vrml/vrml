[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-alert

[English](./README.md) 

[WIKI](./WIKI.md)

Alert支持各种快捷灵活方式的告警API，避免过程式的调用告警service

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-alert</artifactId>
  <version>1.1.3</version>
</dependency>
```

### 发送告警信息到具体处理者actor

定义自定义告警message并绑定到自定义告警执行者actor。

通过`Alerts.tell(message)`将警报message告知警报执行者actor。

然后在`actor.onReceive(message)`中处理告警message。

```java
/**
 * 告警API的演示.
 */
public class AlertsTest {

    /**
     * 定义自定义的 {@code alert actor} 然后注册到上下文当中，例如在spring中使用 {@link Component} 的方式.
     * 继承 {@link AbstractAlertActor} 并绑定具体的告警消息类型.
     */
    @Component
    public static class TestAlertActor extends AbstractAlertActor<TestAlertActor.TestAlertMessage> {

        /**
         * 定义自定义的 {@code alert message}，并绑定到具体的actor中.
         * 需要实现 {@link AlertMessage} 接口，从而能够被 {@link AlertActorSystem} 接收.
         */
        @Data
        @AllArgsConstructor
        public static class TestAlertMessage implements AlertMessage {
            private final String message;
        }

        /**
         * 接收 {@code alert message} 并执行具体处理逻辑.
         */
        @Override
        protected void onReceive(TestAlertMessage message) {
            System.out.println(message.getMessage());
        }
    }

    /**
     * 将 {@code default log alert message} 发送给 {@code default log alert actor}.
     * 将会通过 {@link Slf4j}打印不容日志等级的告警日志信息.
     */
    public void tellDefault() {
        // 发送默认的日志消息
        Alerts.tell(new DefaultLogAlertActor.DefaultLogAlertMessage("alert with error level"));
        // 发送带有日志等级的消息
        Alerts.tell(new DefaultLogAlertActor.DefaultLogAlertMessage("alert with error level", DefaultLogAlertActor.AlertsLogLevelType.ERROR));
        Alerts.tell(new DefaultLogAlertActor.DefaultLogAlertMessage("alert with warn level", DefaultLogAlertActor.AlertsLogLevelType.WARN));
    }

    /**
     * 将 {@code custom alert message} 发送给 {@code custom alert actor}.
     * 将会在 {@code AbstractAlertActor#onReceive(AlertMessage)} 中执行处理逻辑
     */
    public void tellCustom() {
        Alerts.tell(new TestAlertActor.TestAlertMessage("TEST"));
    }

    /**
     * 使用自定义背景更换Spring上下文。 
     * 将 {@code custom alert message} 发送给 {@code custom alert actor}.
     */
    @Deprecated
    public void tellWithCustomContext() {
        // 使用自定义context，而非默认的spring-context
        initCustomContext();
        Alerts.tell(new TestAlertActor.TestAlertMessage("TEST"));
    }
    
    @Deprecated
    private void initCustomContext() {
        // 使用自定义context
        Vrml.builder()
                .useCustomConfig()
                .build();
        // 设置自定义的actor容器
        AlertActorSystem.setCustomActorContainer(Collections.singletonList(new TestAlertActor()));
        // 设置自定义的告警API配置
        AlertActorSystem.setCustomConfiguration(new AlertConfiguration() {
            @Override
            public boolean isAlertAsync() {
                return false;
            }

            @Override
            public boolean isAlertEnable(AlertMessage message) {
                return true;
            }
        });
    }
}
```

### 使用spring上下文或者自定义的上下文管理actor

默认情况下，Vrml使用Spring-Context进行依赖注入。
 
当然，您也可以使用Custom-Context进行依赖项注入，而不是使用spring。

您可以通过以下代码将Alerts API与Custom-Context结合使用。

```java
  Vrml.builder()
          // 使用自定义context
          .useCustomConfig()
          .build();

  // 设置自定义的actor容器
  AlertActorSystem.setCustomActorContainer(Collections.singletonList(new TestAlertActor()));
  // 设置自定义的告警API配置
  AlertActorSystem.setCustomConfiguration(new AlertConfiguration() {
      @Override
      public boolean isAlertAsync() {
          return false;
      }
      @Override
      public boolean isAlertEnable(AlertMessage message) {
          return true;
      }
  });
```

## Example

### 通过告警API发送Email告警消息

```java
// 定义你的Email告警逻辑处理者actor，并绑定Email告警消息类型
public class EmailAlertActor extends AbstractAlertActor<EmailAlertMessage> {
    @Override
    protected void onReceive(EmailAlertMessage message) {
        // 在这里发送Email的逻辑
    }
}

// 定义Email告警消息的具体内容
public class EmailAlertMessage implements AlertMessage {}

// 通过告警API发送Email告警信息
Alerts.tell(new EmailAlertMessage(...));

// Email告警逻辑处理者将会接收此信息，然后执行你的Email发送逻辑
EmailAlertActor.onReceive(message);
```

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-alert).

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-alert</artifactId>
  <version>1.1.3</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.