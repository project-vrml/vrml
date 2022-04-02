package group.rxcloud.vrml.request.config;

import group.rxcloud.vrml.request.RequestConfigurationModule;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static group.rxcloud.vrml.request.RequestConfigurationModule.*;


/**
 * Request configuration.
 * Please provide your custom {@code configuration} through
 * {@link org.springframework.context.annotation.Configuration} and
 * {@link org.springframework.context.annotation.Bean}
 */
public interface RequestConfiguration {

    /**
     * Get request config by name.
     *
     * @param requestName request name
     * @return config or default config
     */
    RequestReportConfig getRequestReportConfig(String requestName);

    /**
     * Request report alert.
     *
     * @param requestName the request report name
     * @param value       the report value
     * @param count       the report count
     */
    default void requestReportAlert(String requestName, String value, Integer count) {
        // default alert take a warn log
        log.warn("[RequestRecordMaxAlert] request[{}] value[{}] count[{}]", requestName, value, count);
    }

    /**
     * The request report value.
     */
    @Getter
    final class RequestReportValue {

        /**
         * Requests cache key.
         */
        private final String requestName;
        /**
         * Requests record switch.
         */
        private final boolean openReport;
        /**
         * Requests statistic value.
         */
        private final String recordValue;
        /**
         * Requests cache strategy.
         */
        private final RequestReportConfig strategy;

        private RequestReportValue(ReportBuilder builder) {
            this.requestName = builder.requestName;
            this.openReport = builder.openReport;
            this.recordValue = builder.recordValue;
            this.strategy = builder.strategy;
        }

        /**
         * The requests report builder.
         */
        @Slf4j
        public static final class ReportBuilder {


            private final String requestName;

            private boolean openReport;
            private String recordValue;
            /**
             * Get config from {@link RequestConfigurationModule#getConfiguration()}
             */
            private boolean isUseConfig;
            private RequestReportConfig strategy;

            /**
             * Instantiates a new Report builder.
             *
             * @param requestName the request name
             */
            public ReportBuilder(String requestName) {
                this.requestName = requestName;
            }

            /**
             * Record value report builder.
             *
             * @param recordValue the record value
             * @return the report builder
             */
            public ReportBuilder recordValue(String recordValue) {
                this.recordValue = recordValue;
                return this;
            }

            /**
             * Use config report builder.
             *
             * @return the report builder
             */
            public ReportBuilder useConfig() {
                this.isUseConfig = true;
                return this;
            }

            /**
             * Strategy report builder.
             *
             * @param strategy the strategy
             * @return the report builder
             */
            public ReportBuilder strategy(RequestReportConfig strategy) {
                this.strategy = strategy;
                return this;
            }

            /**
             * Build request report value.
             *
             * @return the request report value
             */
            public RequestReportValue build() {
                if (this.isUseConfig) {
                    this.strategy = getConfiguration().getRequestReportConfig(requestName);
                }
                if (this.strategy == null) {
                    this.strategy = new RequestReportConfig()
                            .requestReportName(requestName)
                            .openRequestReport(DEFAULT_REPORT_SWITCH)
                            .reportTriggerCount(DEFAULT_TRIGGER_COUNT)
                            .reportExpiredSeconds(DEFAULT_EXPIRE_SECONDS)
                            .reportPoolMaxSize(DEFAULT_MAX_SIZE)
                            .noRecordKeys(new ArrayList<>());
                }
                this.openReport = this.strategy.openRequestReport;
                return new RequestReportValue(this);
            }
        }
    }

    /**
     * Requests report strategy config.
     */
    @Data
    @Accessors(chain = true, fluent = true)
    final class RequestReportConfig {

        /**
         * Request report name as a tag
         */
        private String requestReportName = "";
        /**
         * {@code true} will open the report.
         */
        private boolean openRequestReport = false;
        /**
         * Alert after reaching the trigger count.
         */
        private int reportTriggerCount = 0;
        /**
         * Statistical value expiration time.
         */
        private int reportExpiredSeconds = 0;
        /**
         * Statistical value cache maximum capacity.
         */
        private int reportPoolMaxSize = 0;
        /**
         * Statistics ignored key's value.
         */
        private List<String> noRecordKeys = new ArrayList<>();
    }
}