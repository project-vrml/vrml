package com.ten.func.vavr.trace;

import com.ten.func.vavr.data.ability.Traceable;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Map;

/**
 * The tracers.
 */
public final class Tracers implements Traces {

    private static final ThreadLocalTracer INSTANCE = new ThreadLocalTracer();

    /**
     * Use thread local tracer.
     *
     * @return the tracer
     */
    public static ThreadLocalTracer useThreadLocal() {
        return INSTANCE;
    }

    /**
     * The thread local tracer.
     */
    public static final class ThreadLocalTracer implements Traces.Tracer<Traceable> {

        private ThreadLocal<Traceable> threadLocalTracer;

        /**
         * Instantiates a new Thread local tracer.
         */
        ThreadLocalTracer() {
            this.threadLocalTracer = new ThreadLocal<>();
        }

        // -- INIT

        /**
         * Init.
         *
         * @param scheme the scheme context reference
         */
        @Override
        public void init(Traceable scheme) {
            initTrace(scheme);
        }

        private void initTrace(Traceable schemeReference) {
            threadLocalTracer.set(schemeReference);
        }

        // -- CLEAR

        /**
         * Clear.
         */
        @Override
        public void clear() {
            clearTrace();
        }

        private void clearTrace() {
            threadLocalTracer.remove();
        }

        // -- Trace

        /**
         * Trace.
         *
         * @param traceKey   the trace key
         * @param traceValue the trace value
         */
        @Override
        public void trace(String traceKey, String traceValue) {
            get().addTrace(traceKey, traceValue);
        }

        /**
         * Trace with addition.
         *
         * @param traceKey   the trace key
         * @param traceValue the trace value
         */
        @Override
        public void traceAdd(String traceKey, String traceValue) {
            String origin = get().getTraceMap().getOrDefault(traceKey, Strings.EMPTY);
            get().addTrace(traceKey, origin + "," + traceValue);
        }

        private static final String MESSAGE = "message";

        /**
         * Message.
         *
         * @param message the message
         */
        @Override
        public void message(String message) {
            Traceable traceable = get();
            Map<String, String> traceMap = traceable.getTraceMap();
            String origin = traceMap.get(MESSAGE);
            if (StringUtils.isNotBlank(origin)) {
                traceMap.put(MESSAGE, origin + ", \n\r" + message);
            } else {
                traceMap.put(MESSAGE, message);
            }
        }

        /**
         * Get abstract context.
         *
         * @return the abstract context
         */
        @Override
        public Traceable get() {
            return threadLocalTracer.get();
        }
    }
}
