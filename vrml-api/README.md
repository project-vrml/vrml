[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-api

A Aspect to log request process for [vrml](https://github.com/kevinten10/vrml) library

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-api</artifactId>
  <version>1.0.4</version>
</dependency>
```

### Use [LogsAPI](../vrml-log/README.md)

Provides configurable logging function based on `Logs` module.

The format of the `Api` aspect key is **[className.methodName]**.

### Defined the Aspect

extends `AbstractApiLogInterceptorAspect` and register to bean with `@Aspect.

Implement various hook methods.

```java

/**
 * The Api log interceptor aspect test.
 */
public class ApiLogInterceptorAspectTest {

    /**
     * The num will be add by aspect.
     */
    public static int num = 0;

    /**
     * The Test api log interceptor aspect.
     * All aspect methods will add the num.
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
     * The Test server api.
     */
    @ApiLogInterceptor
    @Component
    public static class TestServerApi {

        /**
         * Server api func.
         */
        public String api(String args) {
            return "args:" + args;
        }
    }

    /**
     * The Test client.
     */
    @Component
    public static class TestClient {

        @Autowired
        private TestServerApi testServerApi;

        /**
         * Call api.
         */
        public void call() {
            System.out.println(testServerApi.api("hello"));
        }
    }

    @Autowired
    private TestClient testClient;

    /**
     * Test.
     */
    public void test() {
        testClient.call();
    }
}
```

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-api).

### Todo

1. Add eventbus listener
2. Statistics start and end time

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-api</artifactId>
  <version>1.0.4</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.