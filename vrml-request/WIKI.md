# Request 网络请求代理API的设计

一个具有代理功能的API，用于对远程请求进行包装，从而进行日志记录/数值记录/响应检查/...

### 问题背景

在项目开发中，网络请求部分存在很多问题。

请问自己：

1.调用接口后，如果未引发任何异常，是否将其视为成功？
2.即使没有抛出异常，您是否考虑了返回状态代码为不成功代码的情况？ 
2.您是否考虑过计算不成功代码的原因和频率？

如果您说“不！”。我认为您需要此API来规范您的行为。这将迫使您首先考虑并提供支持。

### 解决思路

提供了用于调用远程请求的一系列方法，用于快速方便的对以上问题进行检查。

同时提供一套代理规范API，用于统一的日志记录和错误码的生成。

* 通用日志实现
* 提供方便的检查机制
* 强制性的响应码检查
* 提供失败响应码的record记录
* 提供易用的API
 
### 用例演示

#### RequestAPI

Requests API提供了用于调用远程请求的一系列方法。

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

#### Request代理

扩展AbstractRequestProxy以提供请求日志和异常处理。

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

#### Request报表

如果打开报告功能，则远程请求的响应值将在缓存中计数。当达到配置的阈值时，将使用自定义警报方法来发送警报通知。

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

您可以使用`.useConfig（）`从您的自定义spring bean中获取缓存配置。例如，您可以使用远程config-center中的配置。

#### Spring configuration bean
 
如果要使用请求报告功能。您可以使用`.useConfig（）`从您的自定义spring bean中获取缓存配置。

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

例如，您可以使用远程config-center中的配置。远程配置中心文件，例如：

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

#### 这是一个远程请求调用

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

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-request</artifactId>
  <version>1.0.7</version>
</dependency>
```