package group.rxcloud.vrml.request.report;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import group.rxcloud.vrml.core.tags.Fixme;
import group.rxcloud.vrml.request.RequestConfigurationModule;
import group.rxcloud.vrml.request.config.RequestConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static group.rxcloud.vrml.request.RequestConfigurationModule.DEFAULT_EXPIRE_SECONDS;
import static group.rxcloud.vrml.request.RequestConfigurationModule.DEFAULT_MAX_SIZE;

/**
 * The report holder of {@code Requests}.
 */
public class RequestReport {

    private static final Logger log = LoggerFactory.getLogger(RequestReport.class);

    /**
     * The report cache holder.
     */
    private static final Map<String, ReportCache> REPORT_POOL = new ConcurrentHashMap<>();

    /**
     * Register new request report value to its holder.
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
     * Request report alert.
     *
     * @param requestName the request report name
     * @param value       the report value
     * @param count       the report count
     */
    private static void alertReport(String requestName, String value, Integer count) {
        RequestConfigurationModule.getConfiguration().requestReportAlert(requestName, value, count);
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
         * Use {@link Caffeine} cache to record value
         */
        private void initCache() {
            cache = Caffeine.newBuilder()
                    .expireAfterWrite(requestReportConfig.getReportExpiredSeconds() <= 0 ?
                            DEFAULT_EXPIRE_SECONDS :
                            requestReportConfig.getReportExpiredSeconds(), TimeUnit.SECONDS)
                    .maximumSize(requestReportConfig.getReportPoolMaxSize() <= 0 ?
                            DEFAULT_MAX_SIZE :
                            requestReportConfig.getReportPoolMaxSize())
                    .build();
        }

        /**
         * Put the value to statistic pool
         *
         * @param value requests value
         */
        @Fixme(fixme = "optimize synchronized key")
        void put(String value) {
            if (requestReportConfig.getNoRecordKeys().contains(value)) {
                return;
            }
            Runnable alert = null;
            synchronized (requestName) {
                Integer count = cache.getIfPresent(value);
                if (count == null) {
                    // init
                    cache.put(value, 0);
                } else {
                    if (count > requestReportConfig.getReportTriggerCount()) {
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