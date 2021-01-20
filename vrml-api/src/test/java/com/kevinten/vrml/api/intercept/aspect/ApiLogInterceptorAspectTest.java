package com.kevinten.vrml.api.intercept.aspect;

import com.kevinten.vrml.api.intercept.annotation.ApiLogInterceptor;
import com.kevinten.vrml.data.ability.Traceable;
import com.kevinten.vrml.trace.MapTraces;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            logChange();
            return super.requestInvoker(pjp);
        }

        @Override
        protected BaseApiLogContext supplyTraceContext(ProceedingJoinPoint pjp) {
            return new TestContext(pjp);
        }

        @Override
        protected boolean isDoBefore(ProceedingJoinPoint pjp, String logsKey) {
            logChange();
            return super.isDoBefore(pjp, logsKey);
        }

        @Override
        protected void doBefore(ProceedingJoinPoint pjp, String logsKey) {
            logChange();
            super.doBefore(pjp, logsKey);
        }

        @Override
        protected Object doProcess(ProceedingJoinPoint pjp) throws Throwable {
            return super.doProcess(pjp);
        }

        @Override
        protected boolean isDoAfter(ProceedingJoinPoint pjp, Object proceed, String logsKey) {
            logChange();
            return super.isDoAfter(pjp, proceed, logsKey);
        }

        @Override
        protected void doAfter(ProceedingJoinPoint pjp, Object proceed, String logsKey) {
            logChange();
            super.doAfter(pjp, proceed, logsKey);
        }

        @Override
        public boolean isDoException(ProceedingJoinPoint pjp, Throwable throwable, String logsKey) {
            logChange();
            return super.isDoException(pjp, throwable, logsKey);
        }

        @Override
        public Object doException(ProceedingJoinPoint pjp, Throwable throwable, String logsKey) throws Throwable {
            logChange();
            return super.doException(pjp, throwable, logsKey);
        }

        private void logChange() {
            ApiLogInterceptorAspectTest.num++;
            Traceable traceable = MapTraces.useThreadLocal().get();
            traceable.addTrace("key" + ApiLogInterceptorAspectTest.num, "value" + ApiLogInterceptorAspectTest.num);
        }

        public static class TestContext extends BaseApiLogContext {

            public TestContext(ProceedingJoinPoint proceedingJoinPoint) {
                super(proceedingJoinPoint);
            }
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