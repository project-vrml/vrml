[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-alert

[中文](./README_ZH.md)

[WIKI](./WIKI.md)

An alert API that supports multiple methods for [vrml](https://github.com/kevinten10/vrml) library

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-alert</artifactId>
  <version>1.0.7</version>
</dependency>
```

### Tell Alert to Actor

Defined custom alert-message and bind to custom alert-actor.

Tell the alert-message to the alert-actor by `Alerts.tell(message)`.

Then receive and process the alert-message in `actor.onReceive(message)`.

```java
/**
 * The Alerts test.
 */
public class AlertsTest {

    /**
     * Defined your custom {@code alert actor} and register to spring context like {@link Component}.
     * extends {@link AbstractAlertActor} and bind specific alert message type to generic.
     */
    @Component
    public static class TestAlertActor extends AbstractAlertActor<TestAlertActor.TestAlertMessage> {

        /**
         * Defined your custom {@code alert message} which always tied to the actor.
         * impl {@link AlertMessage} so that received by the {@link AlertActorSystem}.
         */
        @Data
        @AllArgsConstructor
        public static class TestAlertMessage implements AlertMessage {
            private final String message;
        }

        /**
         * Receive the {@code alert message} and do your custom alert process.
         */
        @Override
        protected void onReceive(TestAlertMessage message) {
            System.out.println(message.getMessage());
        }
    }

    /**
     * Tell {@code default log alert message} to {@code default log alert actor}.
     * It will logging the alert message by {@link Slf4j} with different log level.
     */
    public void tellDefault() {
        // default log level is ERROR
        Alerts.tell(new DefaultLogAlertActor.DefaultLogAlertMessage("alert with error level"));
        // custom log level inject to constructor
        Alerts.tell(new DefaultLogAlertActor.DefaultLogAlertMessage("alert with error level", DefaultLogAlertActor.AlertsLogLevelType.ERROR));
        Alerts.tell(new DefaultLogAlertActor.DefaultLogAlertMessage("alert with warn level", DefaultLogAlertActor.AlertsLogLevelType.WARN));
    }

    /**
     * Tell {@code custom alert message} to {@code custom alert actor}.
     * It will execute the custom process {@code AbstractAlertActor#onReceive(AlertMessage)}
     */
    public void tellCustom() {
        Alerts.tell(new TestAlertActor.TestAlertMessage("TEST"));
    }

    /**
     * Using custom context replace spring context.
     * Tell {@code custom alert message} to {@code custom alert actor}.
     */
    @Deprecated
    public void tellWithCustomContext() {
        // init custom context replace spring context
        initCustomContext();
        Alerts.tell(new TestAlertActor.TestAlertMessage("TEST"));
    }
    
    @Deprecated
    private void initCustomContext() {
        // set custom context to vrml
        Vrml.builder()
                .useCustomConfig()
                .build();
        // set custom actor container
        AlertActorSystem.setCustomActorContainer(Collections.singletonList(new TestAlertActor()));
        // set custom configuration
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

### Using Custom-Context or Spring-Context

Vrml uses Spring-Context for dependency injection by default.
 
Of course, you can also use Custom-Context for dependency injection instead of using spring.

You can use Alerts API with Custom-Context By the following code.

```java
  Vrml.builder()
          // open custom context
          .useCustomConfig()
          .build();

  // set custom actor container
  AlertActorSystem.setCustomActorContainer(Collections.singletonList(new TestAlertActor()));
  // set custom configuration
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

### Tell alert email by Alerts API

```java
// defined the email alert actor
public class EmailAlertActor extends AbstractAlertActor<EmailAlertMessage> {
    @Override
    protected void onReceive(EmailAlertMessage message) {
        // send email
    }
}

// defined the email alert message
public class EmailAlertMessage implements AlertMessage {}

// tell email alert by Alerts API
Alerts.tell(new EmailAlertMessage(...));

// then email alert actor will receive the alert message and handle it
EmailAlertActor.onReceive(message);
```

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-alert).

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-alert</artifactId>
  <version>1.0.7</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.