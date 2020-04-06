package com.ten.func.vavr.core.tags;

import java.lang.annotation.*;

/**
 * INTERNAL. tagging annotation.
 *
 * @author human
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface TodoUp {

    /*
     * This function is plan to optimize
     */

    /**
     * Optimize desc.
     *
     * @return the desc
     */
    String optimizeDesc() default "todo optimize";
}
