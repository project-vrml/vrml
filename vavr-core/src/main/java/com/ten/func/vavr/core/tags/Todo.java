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
public @interface Todo {

    /*
     * This function is plan to todo
     */

    /**
     * Todo desc.
     *
     * @return todo
     */
    String developDesc() default "todo";
}
