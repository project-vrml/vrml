package com.ten.func.vavr.trace;

import com.ten.func.vavr.data.ability.Traceable;
import com.ten.func.vavr.test.Tests;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test {@link Traces} API.
 */
class TracersTest implements Tests {

    @Test
    public void useThreadLocal() {
        Traceable traceable = new Traceable() {

            private Map<String, String> map = new HashMap<>();

            @Override
            public Map<String, String> getTraceMap() {
                return map;
            }

            @Override
            public void setTraceMap(Map<String, String> traceMap) {
                this.map = traceMap;
            }

            @Override
            public void addTrace(String key, String value) {
                this.map.put(key, value);
            }
        };

        Tracers.useThreadLocal().init(traceable);

        // test equals
        test(() -> {
            Traceable traceable1 = Tracers.useThreadLocal().get();
            Assert.assertEquals(traceable1, traceable);
        });

        // test clear
        test(() -> {
            Tracers.useThreadLocal().clear();
            Traceable traceable1 = Tracers.useThreadLocal().get();
            Assert.assertNull(traceable1);
        });

        Tracers.useThreadLocal().init(traceable);

        // test trace
        test(() -> {
            Tracers.useThreadLocal().trace("test", "value");
            Traceable traceable1 = Tracers.useThreadLocal().get();
            Map<String, String> traceMap = traceable1.getTraceMap();
            Assert.assertEquals("value", traceMap.get("test"));
        });

        // test trace
        test(() -> {
            Tracers.useThreadLocal().message("value");
            Traceable traceable1 = Tracers.useThreadLocal().get();
            Map<String, String> traceMap = traceable1.getTraceMap();
            Assert.assertEquals("value", traceMap.get("message"));
        });
    }
}