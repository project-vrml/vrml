package com.kevinten.vrml.intercept.aspect;

import com.kevinten.vrml.intercept.annotation.ApiLogInterceptor;
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
            ApiLogInterceptorAspectTest.num++;
            return super.requestInvoker(pjp);
        }

        @Override
        protected boolean isDoBefore(ProceedingJoinPoint pjp, String logsKey) {
            ApiLogInterceptorAspectTest.num++;
            return super.isDoBefore(pjp, logsKey);
        }

        @Override
        protected void doBefore(ProceedingJoinPoint pjp, String logsKey) {
            ApiLogInterceptorAspectTest.num++;
            super.doBefore(pjp, logsKey);
        }

        @Override
        protected boolean isDoAfter(ProceedingJoinPoint pjp, Object proceed, String logsKey) {
            ApiLogInterceptorAspectTest.num++;
            return super.isDoAfter(pjp, proceed, logsKey);
        }

        @Override
        protected void doAfter(ProceedingJoinPoint pjp, Object proceed, String logsKey) {
            ApiLogInterceptorAspectTest.num++;
            super.doAfter(pjp, proceed, logsKey);
        }

        @Override
        public boolean isDoException(ProceedingJoinPoint pjp, Throwable throwable, String logsKey) {
            ApiLogInterceptorAspectTest.num++;
            return super.isDoException(pjp, throwable, logsKey);
        }

        @Override
        public Object doException(ProceedingJoinPoint pjp, Throwable throwable, String logsKey) throws Throwable {
            ApiLogInterceptorAspectTest.num++;
            return super.doException(pjp, throwable, logsKey);
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