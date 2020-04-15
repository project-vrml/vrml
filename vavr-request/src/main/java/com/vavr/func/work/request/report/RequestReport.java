package com.vavr.func.work.request.report;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.vavr.func.work.core.tags.TodoUp;
import com.vavr.func.work.request.config.RequestConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * The report holder of {@code Requests}
 */
@Slf4j
public class RequestReport {

    /**
     * The report cache holder
     */
    private static final Map<String, ReportCache> REPORT_POOL = new ConcurrentHashMap<>();

    /**
     * Register new request report value to its holder
     *
     * @param requestReportValue new request report value
     */
    public static void registerRequest(RequestConfiguration.RequestReportValue requestReportValue) {
        RequestReport.REPORT_POOL
                .computeIfAbsent(requestReportValue.getRequestName(),
                        requestName -> new ReportCache(requestName, requestReportValue.getStrategy()))
                .put(requestReportValue.getRecordValue());
    }

    /**
     * Alert report result, you can use {@code Alert} module
     *
     * @param requestName request name
     * @param value       request value
     * @param count       request count
     */
    private static void alertReport(String requestName, String value, Integer count) {
        log.warn("[RequestRecordMaxAlert] request[{}] value[{}] count[{}]", requestName, value, count);
    }

    /**
     * Requests report cache
     */
    private static final class ReportCache {

        private final String requestName;
        private final RequestConfiguration.RequestReportConfig requestReportConfig;

        private Cache<String, Integer> cache;

        /**
         * Instantiates a new Report cache.
         *
         * @param requestName         the request name
         * @param requestReportConfig the request report config
         */
        ReportCache(String requestName, RequestConfiguration.RequestReportConfig requestReportConfig) {
            this.requestName = requestName;
            this.requestReportConfig = requestReportConfig;
            this.initCache();
        }

        /**
         * Use {@link CacheProperties.Caffeine} cache to record value
         */
        private void initCache() {
            cache = Caffeine.newBuilder()
                    .expireAfterWrite(requestReportConfig.reportExpiredSeconds() <= 0 ?
                            RequestConfiguration.DEFAULT_EXPIRE_SECONDS :
                            requestReportConfig.reportExpiredSeconds(), TimeUnit.SECONDS)
                    .maximumSize(requestReportConfig.reportPoolMaxSize() <= 0 ?
                            RequestConfiguration.DEFAULT_MAX_SIZE :
                            requestReportConfig.reportPoolMaxSize())
                    .build();
        }

        /**
         * Put the value to statistic pool
         *
         * @param value requests value
         */
        @TodoUp
        void put(String value) {
            if (requestReportConfig.noRecordKeys().contains(value)) {
                return;
            }
            Runnable alert = null;
            synchronized (requestName) {
                Integer count = cache.getIfPresent(value);
                if (count == null) {
                    // init
                    cache.put(value, 0);
                } else {
                    if (count > requestReportConfig.reportTriggerCount()) {
                        // alert & init
                        cache.put(value, 0);
                        Integer finalCount = count;
                        alert = () -> RequestReport.alertReport(requestName, value, finalCount);
                    } else {
                        // addition
                        cache.put(value, ++count);
                    }
                }
            }
            if (alert != null) {
                // currently run sync
                alert.run();
            }
        }
    }
}