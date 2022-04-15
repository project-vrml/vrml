[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-api

[English](./README.md)

[WIKI](./WIKI.md)

Api模块提供一个可拓展的切面，使用LogsAPI记录网络请求的日志等内容

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-api</artifactId>
  <version>1.0.7</version>
</dependency>
```

### 使用[LogsAPI](../vrml-log/README.md)

基于Logs模块提供可配置的日志记录功能。

Api切面key的格式为[className.methodName]

### 定义Api切面

实现一个自定义的切面类，需要继承基础模板类 `AbstractApiLogInterceptorAspect` 。

然后在通过 `@Aspect` 注解将该切面类声明为切面Bean。

可以实现不同的hook方法，实现自定义的拓展功能。

```java
/**
 * Api切面的演示.
 */
public class ApiLogInterceptorAspectTest {

    /**
     * 统计切面方法调用次数的变量.
     */
    public static int num = 0;

    /**
     * 自定义的切面类. 继承 {@link AbstractApiLogInterceptorAspect} 基础模板类.
     *
     *  所有的切面方法都会使num值+1.
     */
    @Aspect
    @Component
    public static class TestApiLogInterceptorAspect extends AbstractApiLogInterceptorAspect {

        @Override
        public Object requestInvoker(ProceedingJoinPoint pjp) throws Throwable {
            ApiLogInterceptorAspectTest.num++;
            return super.requestInvoker(pjp);
        }

        @Override
        protected boolean isDoBefore(ProceedingJoinPoint pjp) {
            ApiLogInterceptorAspectTest.num++;
            return super.isDoBefore(pjp);
        }

        @Override
        protected void doBefore(ProceedingJoinPoint pjp) {
            ApiLogInterceptorAspectTest.num++;
            super.doBefore(pjp);
        }

        @Override
        protected boolean isDoAfter(ProceedingJoinPoint pjp, Object proceed) {
            ApiLogInterceptorAspectTest.num++;
            return super.isDoAfter(pjp, proceed);
        }

        @Override
        protected void doAfter(ProceedingJoinPoint pjp, Object proceed) {
            ApiLogInterceptorAspectTest.num++;
            super.doAfter(pjp, proceed);
        }

        @Override
        public boolean isDoException(ProceedingJoinPoint pjp, Throwable throwable) {
            ApiLogInterceptorAspectTest.num++;
            return super.isDoException(pjp, throwable);
        }

        @Override
        public Object doException(ProceedingJoinPoint pjp, Throwable throwable) throws Throwable {
            ApiLogInterceptorAspectTest.num++;
            return super.doException(pjp, throwable);
        }
    }

    /**
     * 模拟的服务端API.
     * 
     * 使用 {@link ApiLogInterceptor} 注解设置切面.
     */
    @ApiLogInterceptor
    @Component
    public static class TestServerApi {

        /**
         * 服务端方法.
         */
        public String api(String args) {
            return "args:" + args;
        }
    }

    /**
     * 模拟的客户端API.
     */
    @Component
    public static class TestClient {

        @Autowired
        private TestServerApi testServerApi;

        /**
         * 调用服务端方法.
         */
        public void call() {
            System.out.println(testServerApi.api("hello"));
        }
    }

    @Autowired
    private TestClient testClient;

    /**
     * 启动一次调用过程.
     */
    public void test() {
        testClient.call();
    }
}
```

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-api).

### Todo

1. 增加事件监听机制
2. 统计开始结束时间

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-api</artifactId>
  <version>1.0.7</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.