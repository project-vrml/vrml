package com.vavr.func.work.demo;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Try demo.
 */
@Slf4j
public class TryDemo {

    /**
     * Try catch.
     */
    public void tryCatch() {
        boolean success = false;
        String step1 = null;
        try {
            // 1. step1
            step1 = step1();
            success = true;
        } catch (Exception e) {
            // 2. step3
            log.warn("failure");
            this.step3(e);
        }

        if (success) {
            // 2. step2
            log.info("success");
            this.step2(step1);
        }

        String step4;
        try {
            // 3. step4
            log.warn("step 4");
            step4 = this.step4();
        } catch (Exception e) {
            // 4. step4 recover
            log.error("error, save data");
            step4 = this.save();
        }

        // 5. step4 finally
        System.out.println(step4);
    }

    /**
     * Try step.
     */
    public void tryStep() {
        // 1. step1
        Try<String> step1 = Try.of(this::step1);

        step1.onSuccess(pass -> {
            // 2. step2
            log.info("success");
            this.step2(pass);
        }).onFailure(throwable -> {
            // 2. step3
            log.warn("failure");
            this.step3(throwable);
        });

        // 3. step4
        Try<String> step4 = Try.of(() -> {
            log.warn("step 4");
            return this.step4();
        }).recover(throwable -> {
            log.error("error, save data");
            return this.save();
        });

        // 5. step4 finally
        System.out.println(step4.get());
    }

    /**
     * Resilience4j Try.
     */
//    public List<User> circuitBreakerTimeLimiter(){
//        // 通过注册器获取熔断器的实例
//        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("backendA");
//        CircuitBreakerUtil.getCircuitBreakerStatus("执行开始前：", circuitBreaker);
//        // 创建单线程的线程池
//        ExecutorService pool = Executors.newSingleThreadExecutor();
//        //将被保护方法包装为能够返回Future的supplier函数
//        Supplier<Future<List<User>>> futureSupplier = () -> pool.submit(remoteServiceConnector::process);
//        // 先用限时器包装，再用熔断器包装
//        Callable<List<User>> restrictedCall = TimeLimiter.decorateFutureSupplier(timeLimiter, futureSupplier);
//        Callable<List<User>> chainedCallable = CircuitBreaker.decorateCallable(circuitBreaker, restrictedCall);
//        // 使用Try.of().recover()调用并进行降级处理
//        Try<List<User>> result = Try.of(chainedCallable::call)
//                .recover(CallNotPermittedException.class, throwable ->{
//                    log.info("熔断器已经打开，拒绝访问被保护方法~");
//                    CircuitBreakerUtil.getCircuitBreakerStatus("熔断器打开中", circuitBreaker);
//                    List<User> users = new ArrayList();
//                    return users;
//                })
//                .recover(throwable -> {
//                    log.info(throwable.getLocalizedMessage() + ",方法被降级了~~");
//                    CircuitBreakerUtil.getCircuitBreakerStatus("降级方法中:",circuitBreaker);
//                    List<User> users = new ArrayList();
//                    return users;
//                });
//        CircuitBreakerUtil.getCircuitBreakerStatus("执行结束后：", circuitBreaker);
//        return result.get();
//    }

    private String save() {
        return toString();
    }

    private String step4() {
        return toString();
    }

    private String step3(Throwable throwable) {
        return toString();
    }

    private void step2(String password) {
        return;
    }

    private String step1() {
        return toString();
    }
}
