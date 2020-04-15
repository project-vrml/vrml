package com.vavr.func.work.core.tags;

import java.lang.annotation.*;

/**
 * INTERNAL. tagging annotation.
 *
 * @author human
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface TodoDev {

    /*
     * This function is plan to develop
     */

    /**
     * Develop desc.
     *
     * @return the desc
     */
    String developDesc() default "todo develop";
}
