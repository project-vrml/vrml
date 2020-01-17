package com.ten.func.vavr.test;

/**
 * @author shihaowang
 */
public @interface TestMetric {

    Class<? extends Runnable> metric();
}
