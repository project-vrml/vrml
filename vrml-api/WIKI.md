# Api 接口通用切面的设计

API模块提供一个通用的基准切面，以记录接口的请求响应日志，同时支持配置化，使得更加灵活

### 问题背景

对于网络接口调用，有时我们会需要一个统一的日志切面，用以追踪请求和响应。
当前已有很多日志AOP的实现，能够满足我们基本的需求，那vrml的日志aop能够额外做到什么呢？

### 日志切面设计

同开源的普通日志AOP（下文代称NAOP）实现相比，vrml主要在以下三个方面做了优化和增强：

#### 1. 缩减日志量

NAOP虽然能够拦截接口的请求和响应，但往往对所有接口一视同仁。

对于不重要的接口，仍然会有大量的日志被打印记录，无法做到更精细化的操作。

vrml通过配置化和灵活定制的切入点，使得可以忽略掉自定义的内容，大幅缩减了无效日志量。

#### 2. 增加切入点

vrml的日志API在每个可执行点都提供了hook，实现方可以自由的实现这些hook，从而实现丰富的自定义操作。

##### 提供三种准入检查的hook：

通过定制准入检查点，可以使得满足条件的接口跳过日志记录的步骤，缩减无效日志

1. `isDoBefore`：是否跳过request日志记录
2. `isDoAfter`：是否跳过response日志记录
3. `isDoException`：是否跳过异常时日志记录

##### 提供三种具体操作的hook：

通过重写具体操作，可以自定义日志记录的步骤和姿势。不过在大多数情况下默认的实现足矣

1. `doBefore`：request日志记录操作
2. `doAfter`：response日志记录操作
3. `doException`：异常时日志记录操作

#### 3. 可分层分级的日志配置

vrml的日志API提供动态配置功能。

结合`Logs`API的日志配置功能，使得接口配置的颗粒度精细到每个接口，能够为每个接口提供不同的日志配置。

##### 使用[LogsAPI](../vrml-log/README.md)

基于Logs模块提供可配置的日志记录功能。

Api切面key的格式为[className.methodName]

在此之外还提供 `ApiLogConfiguration` 接口，可以为日志AOP提供全局的配置。

### 用例演示

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

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-api</artifactId>
  <version>1.0.7</version>
</dependency>
```