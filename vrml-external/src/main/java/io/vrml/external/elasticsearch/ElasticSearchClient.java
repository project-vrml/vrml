package io.vrml.external.elasticsearch;

import io.vrml.core.beans.SpringContextConfigurator;
import io.vrml.core.serialization.Serialization;
import io.vrml.external.elasticsearch.config.ElasticSearchConfiguration;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The Elastic search client.
 */
public final class ElasticSearchClient {

    private ElasticSearchClient() {
    }

    private static volatile ElasticSearchQueryRestClient elasticSearchQueryRestClient;
    private static volatile ElasticSearchConfiguration elasticSearchConfiguration;

    /**
     * Update elastic search query rest client from {@link SpringContextConfigurator}.
     */
    public static void updateElasticSearchQueryRestClient() {
        initElasticSearchConfiguration();
        initElasticSearchQueryRestClient();
    }

    static ElasticSearchConfiguration getElasticSearchConfiguration() {
        if (elasticSearchConfiguration == null) {
            initElasticSearchConfiguration();
        }
        return elasticSearchConfiguration;
    }

    static ElasticSearchQueryRestClient getElasticSearchQueryRestClient() {
        if (elasticSearchQueryRestClient == null) {
            initElasticSearchQueryRestClient();
        }
        return elasticSearchQueryRestClient;
    }

    private static void initElasticSearchConfiguration() {
        elasticSearchConfiguration = SpringContextConfigurator.getBean(ElasticSearchConfiguration.class);
        if (elasticSearchConfiguration == null) {
            throw new IllegalArgumentException("ElasticSearchConfiguration is null! Because the creation from the spring-context failed.");
        }
    }

    private static void initElasticSearchQueryRestClient() {
        ElasticSearchQueryRestClient.ElasticSearchRestConfig restConfig = getElasticSearchConfiguration()
                .supplyRestConfig();
        if (restConfig != null) {
            elasticSearchQueryRestClient = ElasticSearchQueryClientFactory.createRestClient(restConfig);
        }
        if (elasticSearchQueryRestClient == null) {
            throw new IllegalArgumentException("ElasticSearchQueryRestClient is null! Because the creation from the configuration failed.");
        }
    }

    // -- public api

    /**
     * Search elasticsearch.
     *
     * @param <T>           the response type
     * @param queryRequest  the query request
     * @param responseClass the response class
     * @return the optional of {@code Tuple2<HitCount, List<Response>>}
     */
    public static <T> Optional<Tuple2<Long, List<T>>> search(ElasticSearchQueryRequest queryRequest, Class<T> responseClass) {
        // get index name
        Optional<String> indexNameOp = getElasticSearchIndexName(responseClass);
        if (!indexNameOp.isPresent()) {
            getElasticSearchConfiguration()
                    .thrownException(queryRequest);
            return Optional.empty();
        }
        // make search request
        SearchRequest searchRequest = Requests
                .searchRequest(indexNameOp.get())
                .source(builderSearchSource(queryRequest));
        try {
            // search
            final ElasticSearchQueryRestClient client = getElasticSearchQueryRestClient();
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            if (response != null) {
                if (response.getHits() != null) {
                    if (response.getHits().getHits() != null &&
                            response.getHits().getHits().length > 0) {
                        // response
                        long totalHits = response.getHits().getTotalHits();
                        List<T> responses = Arrays
                                .stream(response.getHits().getHits())
                                .map(documentFields -> Serialization.GSON.fromJson(documentFields.getSourceAsString(), responseClass))
                                .collect(Collectors.toList());
                        return Optional.of(Tuple.of(totalHits, responses));
                    }
                }
            }
        } catch (IOException e) {
            getElasticSearchConfiguration()
                    .handleExpectedIOException(e, queryRequest);
        } catch (Exception e) {
            getElasticSearchConfiguration()
                    .handleUnexpectedException(e, queryRequest);
        }
        return Optional.empty();
    }

    // -- private

    private static Optional<String> getElasticSearchIndexName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(ElasticSearchIndex.class)) {
            ElasticSearchIndex annotation = clazz.getAnnotation(ElasticSearchIndex.class);
            if (StringUtils.isNotBlank(annotation.index())) {
                return Optional.of((annotation.index()));
            }
        }
        return Optional.empty();
    }

    private static SearchSourceBuilder builderSearchSource(ElasticSearchQueryRequest queryRequest) {
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        // query must terms
        if (!CollectionUtils.isEmpty(queryRequest.getQueryMustTerms())) {
            queryRequest.getQueryMustTerms()
                    .forEach((k, v) -> builder.must(
                            QueryBuilders.termsQuery(k, v)));
        }
        // query should terms
        if (!CollectionUtils.isEmpty(queryRequest.getQueryShouldTerms())) {
            queryRequest.getQueryShouldTerms()
                    .forEach((k, vs) -> vs.forEach(v ->
                            builder.should(
                                    QueryBuilders.termQuery(k, v))));
        }
        // query range terms
        if (!CollectionUtils.isEmpty(queryRequest.getQueryRangeTerms())) {
            queryRequest.getQueryRangeTerms()
                    .forEach(r ->
                            builder.must(
                                    QueryBuilders.rangeQuery(r.getQueryName())
                                            .from(r.getRangeStart())
                                            .to(r.getRangeEnd())));
        }
        return new SearchSourceBuilder()
                .query(builder)
                .aggregation(AggregationBuilders
                        .cardinality(queryRequest.getQueryAggregationCardinalityTerm())
                        .field(queryRequest.getQueryAggregationCardinalityTerm()))
                .size(queryRequest.getQueryPageProp().getPageSize())
                .from(queryRequest.fromIndex())
                .sort(queryRequest.getQuerySortTerm(), queryRequest.getSortOrder());
    }
}
