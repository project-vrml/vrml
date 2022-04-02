package group.rxcloud.vrml.compute.config;

/**
 * The Compute configuration.
 *
 * @param <T> the config type parameter
 */
public interface ComputeConfiguration<T extends ComputeConfiguration.ComputeConfig> {

    /**
     * Gets compute configuration.
     *
     * @param key the key
     * @return the compute configuration
     */
    T getComputeConfiguration(String key);

    /**
     * The Compute config.
     */
    interface ComputeConfig {

        /**
         * Gets key.
         *
         * @return the key
         */
        String getKey();
    }
}
