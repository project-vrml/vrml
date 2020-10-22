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
public @interface Todo {

    /*
     * This function is plan to todo
     */

    /**
     * Todo desc.
     *
     * @return todo
     */
    String todo() default "todo";
}
