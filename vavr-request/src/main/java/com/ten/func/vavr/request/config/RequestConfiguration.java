package com.ten.func.vavr.request.config;

import com.ten.func.vavr.core.beans.SpringContextConfigurator;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Request configuration.
 * Please provide your custom {@code configuration} through
 * {@link org.springframework.context.annotation.Configuration} and
 * {@link org.springframework.context.annotation.Bean}
 */
public interface RequestConfiguration {

    /**
     * The constant DEFAULT_REQUEST_NAME.
     */
    String DEFAULT_REQUEST_NAME = "default";

    /**
     * The constant DEFAULT_REPORT_SWITCH.
     */
    boolean DEFAULT_REPORT_SWITCH = false;

    /**
     * The constant DEFAULT_EXPIRE_SECONDS.
     */
    int DEFAULT_EXPIRE_SECONDS = 100;

    /**
     * The constant DEFAULT_MAX_SIZE.
     */
    int DEFAULT_MAX_SIZE = 1000;

    /**
     * The constant DEFAULT_TRIGGER_COUNT.
     */
    int DEFAULT_TRIGGER_COUNT = 100;

    /**
     * Get config with default
     *
     * @param requestName request name
     * @return config or default config
     */
    RequestReportConfig getRequestReportConfig(String requestName);

    /**
     * The type Request report value.
     */
    @Getter
    final class RequestReportValue {

        /**
         * Requests cache key
         */
        private String requestName;
        /**
         * Requests record switch
         */
        private boolean openReport;
        /**
         * Requests statistic value
         */
        private String recordValue;
        /**
         * Requests cache strategy
         */
        private RequestConfiguration.RequestReportConfig strategy;

        private RequestReportValue(ReportBuilder builder) {
            this.requestName = builder.requestName;
            this.openReport = builder.openReport;
            this.recordValue = builder.recordValue;
            this.strategy = builder.strategy;
        }

        /**
         * The type Report builder.
         */
        @Slf4j
        public static final class ReportBuilder {

            /**
             * Requests strategy configurator
             */
            private static RequestConfiguration configuration;

            private static void initSpringContextConfig() {
                if (configuration == null) {
                    synchronized (RequestConfiguration.class) {
                        if (configuration == null) {
                            // load requests configuration from spring context
                            try {
                                configuration = SpringContextConfigurator.getBean(RequestConfiguration.class);
                            } catch (Exception e) {
                                log.error("Report init spring context configuration failure.", e);
                            }
                        }
                    }
                }
            }

            private static RequestConfiguration getConfiguration() {
                ReportBuilder.initSpringContextConfig();
                return configuration;
            }

            private final String requestName;

            private boolean openReport;

            private String recordValue;
            /**
             * Get config from {@link #configuration}
             */
            private boolean isUseConfig;
            private RequestConfiguration.RequestReportConfig strategy;

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
            public ReportBuilder strategy(RequestConfiguration.RequestReportConfig strategy) {
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
     * Requests report strategy config
     */
    @Data
    @Accessors(chain = true, fluent = true)
    final class RequestReportConfig {
        private String requestReportName;
        private boolean openRequestReport;
        private int reportTriggerCount;
        private int reportExpiredSeconds;
        private int reportPoolMaxSize;
        private List<String> noRecordKeys;
    }
}