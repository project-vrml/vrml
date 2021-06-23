package com.kevinten.vrml.switchs;

import com.google.gson.JsonObject;
import com.kevinten.vrml.core.beans.SpringContextConfigurator;
import com.kevinten.vrml.core.tags.Important;
import io.vavr.CheckedFunction0;
import io.vavr.CheckedRunnable;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public final class Switches implements SwitchApi {

    public static final Switches INSTANCE = new Switches();

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
                    // load metrics configuration from spring context
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

    public static void main(String[] args) {
        SwitchKey switchKey = SwitchKeyBuilder.builder()
                .next("1")
                .next("2")
                .next("3")
                .build();
        List<String> keys = switchKey.getKeys();
        keys.forEach(System.out::println);
    }

    @Override
    public boolean getSwitch(SwitchKey switchKey) {
        List<String> keys = switchKey.getKeys();
        JsonObject header = configuration.getParam();

        for (String key : keys) {
            header = header.getAsJsonObject(key);
            if (header == null) {
                return false;
            }
        }

        return header.getAsBoolean();
    }

    @Override
    public void runWithSwitch(SwitchKey switchKey, Runnable runnable) {
        boolean switchs = getSwitch(switchKey);
        if (switchs) {
            runnable.run();
        }
    }

    @Override
    public void runWithSwitch(SwitchKey switchKey, CheckedRunnable runnable) throws Throwable {
        boolean switchs = getSwitch(switchKey);
        if (switchs) {
            runnable.run();
        }
    }

    @Override
    public <T> T callWithSwitch(SwitchKey switchKey, Supplier<T> supplier) {
        boolean switchs = getSwitch(switchKey);
        if (switchs) {
            return supplier.get();
        }
        return null;
    }

    @Override
    public <T> T callWithSwitch(SwitchKey switchKey, CheckedFunction0<T> function) throws Throwable {
        boolean switchs = getSwitch(switchKey);
        if (switchs) {
            return function.apply();
        }
        return null;
    }

    public interface SwitchKeyBuilder {

        static SwitchKeyWrapper builder() {
            return new SwitchKeyWrapper.DefaultSwitchKeyWrapper();
        }
    }

    public interface SwitchKeyWrapper {

        SwitchKeyWrapper next(String key);

        SwitchKey build();

        class DefaultSwitchKeyWrapper implements SwitchKeyWrapper {

            String key;
            DefaultSwitchKeyWrapper prev;
            DefaultSwitchKeyWrapper next;

            @Override
            public SwitchKeyWrapper next(String key) {
                DefaultSwitchKeyWrapper next = new DefaultSwitchKeyWrapper();
                next.key = key;

                this.next = next;
                next.prev = this;
                return next;
            }

            @Override
            public SwitchKey build() {
                DefaultSwitchKeyWrapper header = this;
                while (header.prev != null) {
                    header = header.prev;
                }
                SwitchKey.DefaultSwitchKey defaultSwitchKey = new SwitchKey.DefaultSwitchKey();
                defaultSwitchKey.headerWrapper = header;
                return defaultSwitchKey;
            }
        }
    }

    public interface SwitchKey {

        List<String> getKeys();

        class DefaultSwitchKey implements SwitchKey {

            List<String> keys;

            SwitchKeyWrapper.DefaultSwitchKeyWrapper headerWrapper;

            @Override
            public List<String> getKeys() {
                if (keys == null) {
                    keys = cacheKeys();
                }
                return keys;
            }

            private List<String> cacheKeys() {
                if (headerWrapper == null) {
                    return Collections.emptyList();
                }
                List<String> keys = new ArrayList<>();
                SwitchKeyWrapper.DefaultSwitchKeyWrapper header = headerWrapper;
                while (header.next != null) {
                    header = header.next;
                    keys.add(header.key);
                }
                return keys;
            }
        }
    }
}


interface SwitchApi {

    boolean getSwitch(Switches.SwitchKey switchKey);

    void runWithSwitch(Switches.SwitchKey switchKey, Runnable runnable);

    void runWithSwitch(Switches.SwitchKey switchKey, CheckedRunnable runnable) throws Throwable;

    <T> T callWithSwitch(Switches.SwitchKey switchKey, Supplier<T> supplier);

    <T> T callWithSwitch(Switches.SwitchKey switchKey, CheckedFunction0<T> function) throws Throwable;
}