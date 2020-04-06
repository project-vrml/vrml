package com.ten.func.vavr.log;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

public interface LogsConfiguration {

    boolean isTraceEnabled(String key);

    boolean isDebugEnabled(String key);

    boolean isInfoEnabled(String key);

    boolean isWarnEnabled(String key);

    boolean isErrorEnabled(String key);

    @Data
    final class LogsConfig {
        private String key;
        private boolean debug = false;
        private boolean info = true;
        private boolean warn = true;
        private boolean error = true;
    }
}


interface LogsModule {

    @Data
    @Component
    final class LogsSettings {

        private List<LogsConfiguration.LogsConfig> logsConfigs;

        public LogsConfiguration.LogsConfig findByKey(String key) {
            return this.logsConfigs.stream()
                    .filter(logsConfig -> logsConfig.getKey().equalsIgnoreCase(key))
                    .findAny()
                    .orElse(null);
        }
    }
}