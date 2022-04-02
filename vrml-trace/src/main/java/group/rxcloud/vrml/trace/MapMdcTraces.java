package group.rxcloud.vrml.trace;

import group.rxcloud.vrml.data.ability.Traceable;
import org.slf4j.MDC;

/**
 * The Traces API with {@code Traceable} and {@code MDC}.
 */
public abstract class MapMdcTraces {

    private static final ThreadLocalMapMdcTracer<Traceable> INSTANCE = new ThreadLocalMapMdcTracer<>();

    /**
     * Use threadLocal tracer.
     *
     * @return the threadLocal tracer
     */
    public static ThreadLocalMapMdcTracer<Traceable> useThreadLocal() {
        return INSTANCE;
    }

    /**
     * The threadLocal tracer.
     *
     * @param <TraceObj> the obj impl {@code Traceable}.
     */
    public static final class ThreadLocalMapMdcTracer<TraceObj extends Traceable> implements MapTracer<TraceObj>, MdcTracer {

        private final ThreadLocal<TraceObj> threadLocalTracer;

        /**
         * Instantiates a new Thread local tracer.
         */
        ThreadLocalMapMdcTracer() {
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

        /**
         * Init mdc.
         *
         * @param mdcKey   the mdc key
         * @param mdcValue the mdc value
         */
        @Override
        public void initMdc(String mdcKey, String mdcValue) {
            MDC.put(mdcKey, mdcValue);
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

        /**
         * Clear mdc.
         */
        @Override
        public void clearMdc() {
            MDC.clear();
        }

        /**
         * Remove mdc.
         *
         * @param mdcKey the mdc key
         */
        @Override
        public void remove(String mdcKey) {
            MDC.remove(mdcKey);
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

        // -- MDC

        /**
         * Put mdc.
         *
         * @param mdcKey   the mdc key
         * @param mdcValue the mdc value
         */
        @Override
        public void put(String mdcKey, String mdcValue) {
            MDC.put(mdcKey, mdcValue);
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
