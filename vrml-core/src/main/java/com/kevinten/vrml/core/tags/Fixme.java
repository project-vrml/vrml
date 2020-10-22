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
public @interface Fixme {

    /*
     * This function is plan to fixme
     */

    /**
     * Fixme desc.
     *
     * @return fixme
     */
    String fixme() default "fixme";
}