package com.kevinten.vrml.external.elasticsearch;

import org.elasticsearch.search.sort.SortOrder;

import java.util.List;
import java.util.Map;

/**
 * The Elastic search query request.
 */
public class ElasticSearchQueryRequest {

    private final String index;
    private final ElasticSearchPageProp queryPageProp;

    private final Map<String, List<Object>> queryMustTerms;
    private final Map<String, List<Object>> queryShouldTerms;

    private final List<ElasticSearchRangeProp> queryRangeTerms;
    private final String querySortTerm;
    private final String queryAggregationCardinalityTerm;

    private final SortOrder sortOrder;

    /**
     * Page from index.
     *
     * @return the page from index.
     */
    public int fromIndex() {
        return this.getQueryPageProp().fromPageIndex();
    }

    // -- Builder

    private ElasticSearchQueryRequest(Builder builder) {
        index = builder.index;
        queryPageProp = builder.queryPageProp;
        queryMustTerms = builder.queryMustTerms;
        queryShouldTerms = builder.queryShouldTerms;
        queryRangeTerms = builder.queryRangeTerms;
        querySortTerm = builder.querySortTerm;
        queryAggregationCardinalityTerm = builder.queryAggregationCardinalityTerm;
        sortOrder = builder.sortOrder == null ? SortOrder.DESC : builder.sortOrder;
    }

    public String getIndex() {
        return index;
    }

    public ElasticSearchPageProp getQueryPageProp() {
        return queryPageProp;
    }

    public List<ElasticSearchRangeProp> getQueryRangeTerms() {
        return queryRangeTerms;
    }

    public Map<String, List<Object>> getQueryMustTerms() {
        return queryMustTerms;
    }

    public Map<String, List<Object>> getQueryShouldTerms() {
        return queryShouldTerms;
    }

    public String getQuerySortTerm() {
        return querySortTerm;
    }

    public String getQueryAggregationCardinalityTerm() {
        return queryAggregationCardinalityTerm;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    /**
     * The Elastic search range prop.
     */
    public static class ElasticSearchRangeProp {

        private final Long rangeStart;
        private final Long rangeEnd;
        private final String queryName;

        private ElasticSearchRangeProp(Builder builder) {
            rangeStart = builder.rangeStart;
            rangeEnd = builder.rangeEnd;
            queryName = builder.queryName;
        }

        public Long getRangeStart() {
            return rangeStart;
        }

        public Long getRangeEnd() {
            return rangeEnd;
        }

        public String getQueryName() {
            return queryName;
        }

        public static final class Builder {

            private Long rangeStart;
            private Long rangeEnd;
            private String queryName;

            public Builder() {
            }

            public Builder rangeStart(Long val) {
                rangeStart = val;
                return this;
            }

            public Builder rangeEnd(Long val) {
                rangeEnd = val;
                return this;
            }

            public Builder queryName(String val) {
                queryName = val;
                return this;
            }

            public ElasticSearchRangeProp build() {
                return new ElasticSearchRangeProp(this);
            }
        }
    }

    /**
     * The Elastic search page prop.
     */
    public static class ElasticSearchPageProp {

        private final int pageNum;
        private final int pageSize;

        /**
         * Page from index.
         *
         * @return the page from index.
         */
        protected int fromPageIndex() {
            int i = (this.getPageNum() - 1) * this.getPageSize();
            return Math.max(i, 0);
        }

        public int getPageNum() {
            return pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        private ElasticSearchPageProp(Builder builder) {
            pageNum = builder.pageNum;
            pageSize = builder.pageSize;
        }

        public static final class Builder {

            private int pageNum;
            private int pageSize;

            public Builder() {
            }

            public Builder pageNum(int val) {
                pageNum = val;
                return this;
            }

            public Builder pageSize(int val) {
                pageSize = val;
                return this;
            }

            public ElasticSearchPageProp build() {
                return new ElasticSearchPageProp(this);
            }
        }
    }

    public static final class Builder {

        private String index;
        private ElasticSearchPageProp queryPageProp;
        private Map<String, List<Object>> queryMustTerms;
        private Map<String, List<Object>> queryShouldTerms;
        private List<ElasticSearchRangeProp> queryRangeTerms;
        private String querySortTerm;
        private String queryAggregationCardinalityTerm;
        private SortOrder sortOrder;

        public Builder() {
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder queryPageProp(ElasticSearchPageProp val) {
            queryPageProp = val;
            return this;
        }

        public Builder queryMustTerms(Map<String, List<Object>> val) {
            queryMustTerms = val;
            return this;
        }

        public Builder queryShouldTerms(Map<String, List<Object>> val) {
            queryShouldTerms = val;
            return this;
        }

        public Builder queryRangeTerms(List<ElasticSearchRangeProp> val) {
            queryRangeTerms = val;
            return this;
        }

        public Builder querySortTerm(String val) {
            querySortTerm = val;
            return this;
        }

        public Builder queryAggregationCardinalityTerm(String val) {
            queryAggregationCardinalityTerm = val;
            return this;
        }

        public Builder sortOrder(SortOrder val) {
            sortOrder = val;
            return this;
        }

        public ElasticSearchQueryRequest build() {
            return new ElasticSearchQueryRequest(this);
        }
    }
}
