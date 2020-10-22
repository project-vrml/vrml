package com.kevinten.vrml.core.tags;

import java.lang.annotation.*;

/**
 * INTERNAL. tagging annotation.
 *
 * @author human
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface Important {

    /*
     * This function is mark important reminders
     */

    /**
     * Mark important reminders.
     *
     * @return important
     */
    String important() default "important!";
}

