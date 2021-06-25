package com.kevinten.vrml.switches;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.kevinten.vrml.core.beans.SpringContextConfigurator;
import com.kevinten.vrml.core.tags.Important;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedRunnable;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * The Switches API.
 */
@Slf4j
public final class Switches implements SwitchApi {

    /**
     * The INSTANCE obj.
     */
    public static final Switches INS = new Switches();

    private Switches() {
    }

    /**
     * Switches configurator
     */
    private static volatile SwitchesConfiguration configuration;

    /**
     * Use spring context to provide dynamic configuration.
     */
    private static void initSpringContextConfig() {
        if (configuration == null) {
            synchronized (Switches.class) {
                if (configuration == null) {
                    // load switches configuration from spring context
                    try {
                        configuration = SpringContextConfigurator.getBean(SwitchesConfiguration.class);
                    } catch (Exception e) {
                        log.error("Switches init spring context configuration failure.", e);
                    }
                }
            }
        }
    }

    @Important(important = "The only way to get spring configuration. Avoid context not loading.")
    private static SwitchesConfiguration getConfiguration() {
        Switches.initSpringContextConfig();
        return configuration;
    }

    // -- public api

    @Override
    public boolean getSwitch(SwitchKey switchKey) {
        final List<String> sortedKeys = switchKey.getSortedKeys();
        JsonObject headerParams = getConfiguration().getParams();

        for (String key : sortedKeys) {
            headerParams = headerParams.getAsJsonObject(key);
            if (headerParams == null) {
                return false;
            }
        }

        return headerParams.getAsBoolean();
    }

    @Override
    public void runWithSwitch(SwitchKey switchKey, Runnable runnable) {
        boolean switchOpen = getSwitch(switchKey);
        if (switchOpen) {
            runnable.run();
        }
    }

    @Override
    public void runWithSwitch(SwitchKey switchKey, CheckedRunnable runnable) throws Throwable {
        boolean switchOpen = getSwitch(switchKey);
        if (switchOpen) {
            runnable.run();
        }
    }

    @Override
    public <T> T callWithSwitch(SwitchKey switchKey, Supplier<T> supplier) {
        boolean switchOpen = getSwitch(switchKey);
        if (switchOpen) {
            return supplier.get();
        }
        return null;
    }

    @Override
    public <T> T callWithSwitch(SwitchKey switchKey, CheckedFunction0<T> function) throws Throwable {
        boolean switchOpen = getSwitch(switchKey);
        if (switchOpen) {
            return function.apply();
        }
        return null;
    }

    @Override
    public <T> T callWithSwitchOrDefault(SwitchKey switchKey, Supplier<T> supplier, T defaultValue) {
        boolean switchOpen = getSwitch(switchKey);
        if (switchOpen) {
            return supplier.get();
        }
        return defaultValue;
    }

    @Override
    public <T> T callWithSwitchOrDefault(SwitchKey switchKey, CheckedFunction0<T> function, T defaultValue) throws Throwable {
        boolean switchOpen = getSwitch(switchKey);
        if (switchOpen) {
            return function.apply();
        }
        return defaultValue;
    }

    /**
     * The Switch key builder.
     */
    public interface SwitchKeyBuilder {

        /**
         * Generate switch key wrapper builder chain.
         *
         * @return the header switch key wrapper
         * @apiNote {@code header} key is empty.
         */
        static SwitchKeyWrapper builder() {
            SwitchKeyWrapper.DefaultSwitchKeyWrapper headerOfChainWithEmptyKey =
                    new SwitchKeyWrapper.DefaultSwitchKeyWrapper();
            return headerOfChainWithEmptyKey;
        }
    }

    /**
     * The Switch key wrapper.
     */
    public interface SwitchKeyWrapper {

        /**
         * Generate next switch key wrapper.
         *
         * @param key the key
         * @return the next switch key wrapper
         */
        SwitchKeyWrapper next(String key);

        /**
         * Build the finally switch key.
         *
         * @return the finally switch key
         */
        SwitchKey build();

        /**
         * The Default switch key wrapper.
         */
        class DefaultSwitchKeyWrapper implements SwitchKeyWrapper {

            /**
             * The Key.
             */
            String key;
            /**
             * The Prev node.
             */
            DefaultSwitchKeyWrapper prev;
            /**
             * The Next node.
             */
            DefaultSwitchKeyWrapper next;

            @Override
            public SwitchKeyWrapper next(String key) {
                Objects.requireNonNull(key, "Switch key is null!");

                DefaultSwitchKeyWrapper nextNode = new DefaultSwitchKeyWrapper();
                nextNode.key = key;
                this.next = nextNode;
                nextNode.prev = this;
                return nextNode;
            }

            @Override
            public SwitchKey build() {
                DefaultSwitchKeyWrapper header = this;
                while (header.prev != null) {
                    header = header.prev;
                }

                SwitchKey.DefaultSwitchKey switchKey = new SwitchKey.DefaultSwitchKey();
                switchKey.headerWrapper = header;
                return switchKey;
            }
        }
    }

    /**
     * The Switch key.
     */
    public interface SwitchKey {

        /**
         * Gets keys by sorted.
         *
         * @return the keys
         */
        List<String> getSortedKeys();

        /**
         * The Default switch key.
         */
        class DefaultSwitchKey implements SwitchKey {

            /**
             * The Keys.
             */
            List<String> keys;

            /**
             * The Header wrapper.
             *
             * @apiNote {@code header} key is empty.
             */
            SwitchKeyWrapper.DefaultSwitchKeyWrapper headerWrapper;

            @Override
            public List<String> getSortedKeys() {
                if (keys == null) {
                    synchronized (this) {
                        if (keys == null) {
                            keys = cacheKeys();
                        }
                    }
                }
                return keys;
            }

            // cache immutable list
            private List<String> cacheKeys() {
                if (headerWrapper == null) {
                    return Collections.emptyList();
                }

                ImmutableList.Builder<String> builder = ImmutableList.builder();
                SwitchKeyWrapper.DefaultSwitchKeyWrapper header = headerWrapper;
                while (header.next != null) {
                    header = header.next;
                    if (header.key != null) {
                        builder.add(header.key);
                    }
                }
                return builder.build();
            }
        }
    }
}


/**
 * The {@code Switch} module public api.
 */
interface SwitchApi {

    /**
     * Gets switch boolean value. {@code true} will open the switch.
     *
     * @param switchKey the switch key
     * @return the switch boolean value
     */
    boolean getSwitch(Switches.SwitchKey switchKey);

    /**
     * Run with switch. run when switch is {@code true}.
     *
     * @param switchKey the switch key
     * @param runnable  the runnable
     */
    void runWithSwitch(Switches.SwitchKey switchKey, Runnable runnable);

    /**
     * Run with switch. run when switch is {@code true}.
     *
     * @param switchKey the switch key
     * @param runnable  the runnable
     * @throws Throwable the throwable
     */
    void runWithSwitch(Switches.SwitchKey switchKey, CheckedRunnable runnable) throws Throwable;

    /**
     * Call with switch. call when switch is {@code true}.
     *
     * @param <T>       the type parameter
     * @param switchKey the switch key
     * @param supplier  the supplier
     * @return the call result
     */
    <T> T callWithSwitch(Switches.SwitchKey switchKey, Supplier<T> supplier);

    /**
     * Call with switch. call when switch is {@code true}.
     *
     * @param <T>       the type parameter
     * @param switchKey the switch key
     * @param function  the function
     * @return the function apply result
     * @throws Throwable the throwable
     */
    <T> T callWithSwitch(Switches.SwitchKey switchKey, CheckedFunction0<T> function) throws Throwable;

    /**
     * Call with switch. call when switch is {@code true}. default value when switch is {@code false}.
     *
     * @param <T>          the type parameter
     * @param switchKey    the switch key
     * @param supplier     the supplier
     * @param defaultValue the default value
     * @return the call result
     */
    <T> T callWithSwitchOrDefault(Switches.SwitchKey switchKey, Supplier<T> supplier, T defaultValue);

    /**
     * Call with switch. call when switch is {@code true}. default value when switch is {@code false}.
     *
     * @param <T>          the type parameter
     * @param switchKey    the switch key
     * @param function     the function
     * @param defaultValue the default value
     * @return the function apply result
     * @throws Throwable the throwable
     */
    <T> T callWithSwitchOrDefault(Switches.SwitchKey switchKey, CheckedFunction0<T> function, T defaultValue) throws Throwable;
}