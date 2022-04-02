package group.rxcloud.vrml.external.elasticsearch;

import group.rxcloud.vrml.core.serialization.Serialization;
import group.rxcloud.vrml.external.elasticsearch.config.ElasticSearchConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.vavr.Tuple2;
import lombok.Data;
import mockit.Mock;
import mockit.MockUp;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ElasticSearchClientTest {

    /**
     * Test properties
     */
    private static final String TEST_INDEX = "@name";
    private static final String TEST_RANGE_TERM = "@createTime";
    private static final String TEST_AGGRE_TERM = "@id";
    private static final String TEST_ADDRESS = "@address";
    private static final int TEST_PORT = 80;
    private static final String TEST_PROTOCOL = "http";
    private static final String TEST_BASEAUTH = "@auth";

    /**
     * Test request
     */
    private ElasticSearchQueryRequest queryRequest;

    @Before
    public void before() {
        queryRequest = new ElasticSearchQueryRequest.Builder()
                .index(TEST_INDEX)
                .queryPageProp(new ElasticSearchQueryRequest.ElasticSearchPageProp.Builder()
                        .pageNum(1)
                        .pageSize(10)
                        .build())
                .queryRangeTerms(Lists.newArrayList(
                        new ElasticSearchQueryRequest.ElasticSearchRangeProp.Builder()
                                .queryName(TEST_RANGE_TERM)
                                .rangeStart(System.currentTimeMillis() - 1000 * 60 * 60 * 24)
                                .rangeEnd(System.currentTimeMillis())
                                .build()))
                .queryMustTerms(Maps.newHashMap())
                .queryShouldTerms(Maps.newHashMap())
                .querySortTerm(TEST_RANGE_TERM)
                .queryAggregationCardinalityTerm(TEST_AGGRE_TERM)
                .build();
        mock();
    }

    @Test
    public void search() {
        Optional<Tuple2<Long, List<TestElasticSearchResponse>>> search = ElasticSearchClient.search(queryRequest, TestElasticSearchResponse.class);
        search.ifPresent(longListTuple2 -> {
            System.out.println(longListTuple2._1);
            System.out.println(longListTuple2._2);
        });
    }

    /**
     * Test response
     */
    @Data
    @ElasticSearchIndex(index = TEST_INDEX)
    private static class TestElasticSearchResponse {

        private Long createTime;
    }

    /**
     * Mock spring context
     */
    public void mock() {
        new MockUp<ElasticSearchClient>() {

            @Mock
            ElasticSearchConfiguration getElasticSearchConfiguration() {
                return MockElasticSearchConfiguration.INSTANCE;
            }

            @Mock
            ElasticSearchQueryRestClient getElasticSearchQueryRestClient() {
                return new ElasticSearchQueryRestClient(MockElasticSearchConfiguration.INSTANCE.supplyRestConfig());
            }
        };
    }

    /**
     * Mock configuration
     */
    private static class MockElasticSearchConfiguration implements ElasticSearchConfiguration {

        private static final MockElasticSearchConfiguration INSTANCE = new MockElasticSearchConfiguration();

        @Override
        public ElasticSearchQueryRestClient.ElasticSearchRestConfig supplyRestConfig() {
            ElasticSearchQueryRestClient.ElasticSearchRestConfig mockElasticSearchRestConfig = new ElasticSearchQueryRestClient.ElasticSearchRestConfig();
            mockElasticSearchRestConfig.setAddress(TEST_ADDRESS);
            mockElasticSearchRestConfig.setPort(TEST_PORT);
            mockElasticSearchRestConfig.setProtocol(TEST_PROTOCOL);
            mockElasticSearchRestConfig.setBaseAuth(TEST_BASEAUTH);
            mockElasticSearchRestConfig.setSocketTimeout(10000);
            return mockElasticSearchRestConfig;
        }

        @Override
        public void thrownException(ElasticSearchQueryRequest queryRequest) {
            System.out.println("[TEST][ElasticSearchClientTest.test] queryRequest:" + Serialization.toJsonSafe(queryRequest));
        }

        @Override
        public void handleExpectedIOException(IOException e, ElasticSearchQueryRequest queryRequest) {
            System.out.println("[TEST][ElasticSearchClientTest.test] queryRequest:" + Serialization.toJsonSafe(queryRequest));
            e.printStackTrace();
        }

        @Override
        public void handleUnexpectedException(Exception e, ElasticSearchQueryRequest queryRequest) {
            System.out.println("[TEST][ElasticSearchClientTest.test] queryRequest:" + Serialization.toJsonSafe(queryRequest));
            e.printStackTrace();
        }
    }
}