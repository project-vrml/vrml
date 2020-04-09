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
public @interface Note {

    /*
     * This function is mark note
     */

    /**
     * Mark note
     *
     * @return note
     */
    String description() default "note";
}
