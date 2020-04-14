package com.vavr.func.work.demo;

import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

/**
 * Try.
 */
@Slf4j
public class TryTest {

    /**
     * Try catch.
     */
    public void tryCatch() {
        try {
            // 1. step1
            String step1 = step1();

            // 2. step2
            log.info("success");
            this.step2(step1);
        } catch (Exception e) {
            // 2. step3
            log.warn("failure");
            this.step3(e);
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
        });
        // 4. step4 recover
        step4.recover(throwable -> {
            log.error("error, save data");
            return this.save();
        });

        // 5. step4 finally
        System.out.println(step4.get());
    }

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
