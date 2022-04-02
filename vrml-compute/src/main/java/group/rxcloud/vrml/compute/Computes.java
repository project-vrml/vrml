package group.rxcloud.vrml.compute;

import group.rxcloud.vrml.compute.config.ComputeConfiguration;
import group.rxcloud.vrml.core.beans.SpringContextConfigurator;
import group.rxcloud.vrml.core.tags.Important;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;

/**
 * The Computes API.
 *
 * @param <T> the configuration type parameter
 */
@Slf4j
public abstract class Computes<T extends ComputeConfiguration> implements Compute {

    /**
     * The {@link TimeCounterComputes} singleton.
     */
    public static final TimeCounterComputes TC = new TimeCounterComputes();

    T configuration;

    /**
     * Use spring context to provide dynamic configuration.
     */
    private void initSpringContextConfig() {
        if (configuration == null) {
            synchronized (Computes.class) {
                if (configuration == null) {
                    // load computes configuration from spring context
                    try {
                        Class<? extends ComputeConfiguration> actualTypeArgument =
                                (Class<? extends ComputeConfiguration>)
                                        (((ParameterizedType) getClass().getGenericSuperclass())
                                                .getActualTypeArguments()[0]);
                        configuration = (T) SpringContextConfigurator.getBean(actualTypeArgument);
                    } catch (Exception e) {
                        log.error("Computes init spring context configuration failure.", e);
                    }
                }
            }
        }
    }

    @Important(important = "The only way to get spring configuration. Avoid context not loading.")
    protected T getConfiguration() {
        initSpringContextConfig();
        return configuration;
    }
}
