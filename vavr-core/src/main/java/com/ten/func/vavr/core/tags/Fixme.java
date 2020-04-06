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
public @interface Fixme {

    /*
     * This function is plan to fixme
     */

    /**
     * Fixme desc.
     *
     * @return fixme
     */
    String developDesc() default "fixme";
}