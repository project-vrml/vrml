package com.ten.func.vavr.work.test;

import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test function helper
 *
 * @author shihaowang
 * @date 2019/12/10
 */
public interface TestFunction extends TestConfiguration {

    Logger logger = LoggerFactory.getLogger(TestFunction.class);

    static void main(String[] args) {
    }

    /**
     * Metric test failed msg when exception
     */
    default void test(CheckedRunnable test) {
        Try.run(test).onFailure(metricException).get();
    }

    /**
     * Only test in localhost for one-drive test
     */
    default void local(CheckedRunnable localTest) {
        if (openOneDriveTest) {
            test(localTest);
        }
    }

    /*
     * Value expectation checker in unit test
     */

    // -------------------------------------------------------------- set value

    default void setValue(Runnable setter) {
        setter.run();
    }

    default void setIllegalValue(Runnable setter) {
        setter.run();
    }

    default void setUnusualValue(Runnable setter) {
        setter.run();
    }

    // -------------------------------------------------------------- check value

    default void checkValue(Runnable checker) {
        checker.run();
    }

    default void checkIllegalValue(CheckedRunnable checker) {
        try {
            checker.run();
        } catch (Throwable throwable) {
            logger.error("[IllegalValue] does not meet expectations when [checkIllegalValue]", throwable);
            throw new RuntimeException(throwable);
        }
    }

    default void checkUnusualValue(CheckedRunnable checker) {
        try {
            checker.run();
        } catch (Throwable throwable) {
            logger.error("[UnusualValue] does not meet expectations when [checkUnusualValue]", throwable);
            throw new RuntimeException(throwable);
        }
    }
}
