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
public @interface Important {

    /*
     * This function is mark important reminders
     */

    /**
     * Mark important reminders
     *
     * @return important
     */
    String description() default "important!";
}

