package io.vrml.trace;

import io.vrml.data.ability.Traceable;

/**
 * The Traces API with {@code Traceable}.
 */
public abstract class MapTraces {

    private static final ThreadLocalMapTracer<Traceable> INSTANCE = new ThreadLocalMapTracer<>();

    /**
     * Use threadLocal {@code Traceable} tracer.
     *
     * @return the threadLocal {@code Traceable} tracer
     */
    public static ThreadLocalMapTracer<Traceable> useThreadLocal() {
        return INSTANCE;
    }

    /**
     * The threadLocal {@code Traceable} tracer.
     *
     * @param <TraceObj> the obj impl {@code Traceable}.
     */
    public static final class ThreadLocalMapTracer<TraceObj extends Traceable> implements MapTracer<TraceObj> {

        private final ThreadLocal<TraceObj> threadLocalTracer;

        /**
         * Instantiates a new Thread local tracer.
         */
        ThreadLocalMapTracer() {
            this.threadLocalTracer = new ThreadLocal<>();
        }

        // -- INIT

        /**
         * Init obj.
         *
         * @param traceObj the trace obj
         */
        @Override
        public void initObj(TraceObj traceObj) {
            initTrace(traceObj);
        }

        private void initTrace(TraceObj traceObj) {
            threadLocalTracer.set(traceObj);
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

        // -- TRACE

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
            String origin = get().getTraceMap().getOrDefault(traceKey, "");
            get().addTrace(traceKey, origin + "," + traceValue);
        }

        /**
         * Get trace obj.
         *
         * @return the trace obj
         */
        @Override
        public TraceObj get() {
            return threadLocalTracer.get();
        }
    }
}
