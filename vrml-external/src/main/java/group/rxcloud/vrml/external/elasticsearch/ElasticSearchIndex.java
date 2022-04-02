package group.rxcloud.vrml.external.elasticsearch;

import java.lang.annotation.*;

/**
 * The Elastic search index annotation.
 *
 * @author human
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ElasticSearchIndex {

    /**
     * Index name.
     *
     * @return the index name
     */
    String index();
}
