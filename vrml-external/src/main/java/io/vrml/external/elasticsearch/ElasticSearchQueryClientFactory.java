package io.vrml.external.elasticsearch;

/**
 * The Elastic search query client factory.
 */
public class ElasticSearchQueryClientFactory {

    /**
     * Create rest client elastic search query rest client.
     *
     * @param elasticSearchRestConfig the elastic search rest config
     * @return the elastic search query rest client
     */
    public static ElasticSearchQueryRestClient createRestClient(ElasticSearchQueryRestClient.ElasticSearchRestConfig elasticSearchRestConfig){
        return new ElasticSearchQueryRestClient(elasticSearchRestConfig);
    }
}
