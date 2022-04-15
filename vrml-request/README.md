[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-request

A proxy API to wrap remote request with log/record/check/... for [vrml](https://github.com/kevinten10/vrml) library

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-request</artifactId>
  <version>1.0.7</version>
</dependency>
```

### Request API

The Requests API provides a series of methods for packaging remote requests.

```java
  // response
    Response response = Requests.of(() -> {
              // start a remote request process
              return Client.request(requestObj);
          })
          .check(response -> {
              // check response code and others
              if (Math.random() > 0.5) {
                  // you can throws exception when check illegal
                  throw new RuntimeException();
              }
          })
          .record(o -> {
              // record response to report cache
              return new RequestConfiguration.RequestReportValue
                      // request name
                      .ReportBuilder("default")
                      // request value (you can supply response code)
                      .recordValue(o.getClass().getSimpleName())
                      // use remote config (autowired by spring bean)
                      .useConfig()
                      .build();
          })
          // throws when failure / get value when success
          .get();
```

### Request proxy

Extend the AbstractRequestProxy to provide request logs and exception handling.

```java
// 继承AbstractHttpRequestProxy提供通用的Http-Proxy能力
@Component
public class HttpProxy extends AbstractHttpRequestProxy<Request, Response> {

    // 使用Http服务
    private static final ServiceClient SERVICE_CLIENT = ServiceClient.getInstance();

    // 提供日志埋点的前缀名
    @Override
    protected String requestName() {
        return "ServiceClient.getResponse";
    }
    
    // 使用RequestsAPI进行Http操作，基于Requests提供的能力对响应结果进行检查
    @Override
    protected Response invokeRequest(Request Request) throws Exception {
        return Requests.of(() -> SERVICE_CLIENT.getResponse(Request))
                .check(Response -> {
                    this.assertResponseNotNull(Response);
                    this.assertResponseStatusSuccess(Response);
                    this.assertNotNull(Response.getResult(), "result");
                    this.assertCodeSuccess(Response.getResult().getCode(), ErrorCodes.DEPENDENT_SERVICE_CODE_ERROR);
                })
                .get();
    }
    
    // 自定义ErrorCode
    @Override
    protected ErrorCodes dependentErrorCode() {
        return ErrorCodes.QUERY_SERVICE_ERROR;
    }
}
```

### Request report

If the report function is turned on, the response value of the remote request will be counted in the cache. 
When the configured threshold is reached, a custom alarm method will be used to send an alarm notification.

```java
  // open record function
  .record(o -> {
      // record response to report cache
      return new RequestConfiguration.RequestReportValue
              // request name as cache key
              .ReportBuilder("default")
              // request value (you can supply response code)
              .recordValue(response.code)
              // use remote config (autowired by spring bean)
              .useConfig()
              .build();
  })
```

You can use `.useConfig()` to get the cache config from your custom spring bean. 
For example, you can use the configuration from remote config-center.

### Spring configuration bean
 
If you want to use request-report function.

You can use `.useConfig()` to get the cache config from your custom spring bean. 

```java
  /**
   * Request configuration.
   * Please provide your custom {@code configuration} through
   * {@link Configuration} and
   * {@link org.springframework.context.annotation.Bean}
   */
  @Configuration
  public class YourCustomSpringBean implements RequestConfiguration {
  }
```

For example, you can use the configuration from remote config-center.

Remote config-center file like:

```json
[
  {
    "requestReportName": "default",
    "openRequestReport": true,
    "reportTriggerCount": 100,
    "reportExpiredSeconds": 1000,
    "reportPoolMaxSize": 1000,
    "noRecordKeys": [
      "0"
    ]
  }
]
```

## Example

### This is a remote request invoke.

```java
  // init mode -> ignore
  Requests
          .of(() -> {
              requestType.setMode(FIRST_REQUEST_MODE);
              return client.invokeRequest(requestType);
          })
          .onSuccess(responseType -> {
              try {
                  Thread.sleep(FIRST_CALL_WAIT_TIME);
              } catch (InterruptedException e) {
                  throw throwsError(ErrorCodes.SYSTEM_PROCESS_ERROR, e);
              }
          })
          .recover(throwable -> null);

  // first mode -> normal
  return Requests
          .of(() -> {
              requestType.setMode(FORMAL_REQUEST_MODE);
              return client.invokeRequest(requestType);
          })
          // check response status
          .check(responseType -> {
              this.assertNotNull(responseType, "response");
              this.assertNotNull(responseType.getResponseStatus(), "responseStatus");
              this.assertNotNull(responseType.getResponseHead(), "responseHead");
          })
          // record response code
          .record(responseType -> new RequestConfiguration.RequestReportValue
                  .ReportBuilder(REPORT)
                  .recordValue(responseType.getResponseHead().getErrorCode())
                  .useConfig()
                  .build())
          // check response code
          .check(responseType -> {
              this.assertCodeSuccess(responseType.getResponseHead().getErrorCode(), ErrorCodes.DEPENDENT_SERVICE_CODE_ERROR);
          })
          .get();
``` 

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-request).

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-request</artifactId>
  <version>1.0.7</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.