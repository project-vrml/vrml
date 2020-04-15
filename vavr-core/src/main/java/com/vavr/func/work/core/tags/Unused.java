package com.vavr.func.work.core.tags;

import java.lang.annotation.*;

/**
 * INTERNAL. tagging annotation.
 *
 * @author human
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface Unused {

    /*
     * This function is unused ragging
     */
}
