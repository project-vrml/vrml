package group.rxcloud.vrml.core.tags;

import java.lang.annotation.*;

/**
 * INTERNAL. tagging annotation.
 *
 * @author human
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface Unused {

    /*
     * This function is unused ragging
     */

    /**
     * Unused desc.
     *
     * @return desc
     */
    String unused() default "unused";
}
