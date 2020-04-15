package com.vavr.func.work.data.ability;

import java.util.Map;

/**
 * Allow the context to record tracing data for obj
 */
public interface Traceable {

    // TRACE

    /**
     * Returns tracing data in the map.
     *
     * @return tracing data map
     */
    Map<String, String> getTraceMap();

    /**
     * Sets tracing data map.
     *
     * @param traceMap the trace map
     */
    void setTraceMap(Map<String, String> traceMap);

    /**
     * Add {@code key,value} to the tracing data map.
     *
     * @param key   key
     * @param value value
     */
    void addTrace(String key, String value);
}
