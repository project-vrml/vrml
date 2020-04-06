package com.ten.func.vavr.test;

import com.google.gson.Gson;
import com.ten.func.vavr.test.config.TestConfiguration;
import io.vavr.CheckedRunnable;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test function helper。
 */
public interface Tests extends TestConfiguration {

    Gson GSON = new Gson();

    Logger logger = LoggerFactory.getLogger(Tests.class);

    /**
     * Metric test failed msg when exception
     *
     * @throws RuntimeException when exception
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

    /**
     * Set value
     */
    default void setValue(Runnable setter) {
        setter.run();
    }

    /**
     * Set illegal value
     */
    default void setIllegalValue(Runnable setter) {
        setter.run();
    }

    /**
     * Set unusual value
     */
    default void setUnusualValue(Runnable setter) {
        setter.run();
    }

    // -------------------------------------------------------------- check value

    /**
     * Check value is expected
     */
    default void checkValue(Runnable checker) {
        checker.run();
    }

    /**
     * Check illegal value is expected
     */
    default void checkIllegalValue(CheckedRunnable checker) {
        try {
            checker.run();
        } catch (Throwable throwable) {
            logger.error("[IllegalValue] does not meet expectations when [checkIllegalValue]", throwable);
            throw new RuntimeException(throwable);
        }
    }

    /**
     * Check unusual value is expected
     */
    default void checkUnusualValue(CheckedRunnable checker) {
        try {
            checker.run();
        } catch (Throwable throwable) {
            logger.error("[UnusualValue] does not meet expectations when [checkUnusualValue]", throwable);
            throw new RuntimeException(throwable);
        }
    }
}
