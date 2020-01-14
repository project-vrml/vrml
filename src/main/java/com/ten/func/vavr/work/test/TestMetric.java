package com.ten.func.vavr.work.test;

/**
 * @author shihaowang
 */
public @interface TestMetric {

    Class<? extends Runnable> metric();
}
