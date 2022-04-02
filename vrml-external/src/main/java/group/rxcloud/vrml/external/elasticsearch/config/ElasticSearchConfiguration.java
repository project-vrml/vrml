package group.rxcloud.vrml.external.elasticsearch.config;

import group.rxcloud.vrml.external.elasticsearch.ElasticSearchQueryRequest;
import group.rxcloud.vrml.external.elasticsearch.ElasticSearchQueryRestClient;

import java.io.IOException;

/**
 * The Elastic search configuration.
 */
public interface ElasticSearchConfiguration {

    /**
     * Supply rest config elastic search query rest client .
     *
     * @return the elastic search query rest client
     */
    ElasticSearchQueryRestClient.ElasticSearchRestConfig supplyRestConfig();

    /**
     * Thrown exception.
     *
     * @param queryRequest the query request
     */
    void thrownException(ElasticSearchQueryRequest queryRequest);

    /**
     * Handle expected {@code IOException}.
     *
     * @param e            the {@code IOException}
     * @param queryRequest the query request
     */
    void handleExpectedIOException(IOException e, ElasticSearchQueryRequest queryRequest);

    /**
     * Handle unexpected {@code Exception}.
     *
     * @param e            the {@code Exception}
     * @param queryRequest the query request
     */
    void handleUnexpectedException(Exception e, ElasticSearchQueryRequest queryRequest);
}
