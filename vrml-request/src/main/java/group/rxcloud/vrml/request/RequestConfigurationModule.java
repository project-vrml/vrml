package group.rxcloud.vrml.request;

import group.rxcloud.vrml.core.beans.SpringContextConfigurator;
import group.rxcloud.vrml.request.config.RequestConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Request configuration package module.
 */
public abstract class RequestConfigurationModule {

    /**
     * The package log.
     */
    public static final Logger log = LoggerFactory.getLogger(RequestConfigurationModule.class);

    /**
     * The constant DEFAULT_REQUEST_NAME.
     */
    public static final String DEFAULT_REQUEST_NAME = "default";

    /**
     * The constant DEFAULT_REPORT_SWITCH.
     */
    public static final boolean DEFAULT_REPORT_SWITCH = false;

    /**
     * The constant DEFAULT_EXPIRE_SECONDS.
     */
    public static final int DEFAULT_EXPIRE_SECONDS = 100;

    /**
     * The constant DEFAULT_MAX_SIZE.
     */
    public static final int DEFAULT_MAX_SIZE = 1000;

    /**
     * The constant DEFAULT_TRIGGER_COUNT.
     */
    public static final int DEFAULT_TRIGGER_COUNT = 100;

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
                        log.error("[Vrml]Requests init spring context configuration failure.", e);
                    }
                }
            }
        }
    }

    /**
     * Gets configuration from spring.
     *
     * @return the configuration bean
     */
    public static RequestConfiguration getConfiguration() {
        RequestConfigurationModule.initSpringContextConfig();
        return configuration;
    }
}
