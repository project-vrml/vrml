package com.ten.func.vavr.trace;

/**
 * Traces API.
 */
public interface Traces {

    /**
     * The interface Tracer.
     *
     * @param <TraceObj> the trace parameter
     */
    interface Tracer<TraceObj> {

        /**
         * Init.
         *
         * @param traceObj the trace obj
         */
        void init(TraceObj traceObj);

        /**
         * Clear.
         */
        void clear();

        /**
         * Get trace obj.
         *
         * @return the trace obj
         */
        TraceObj get();

        // -- Trace

        /**
         * Trace.
         *
         * @param traceKey   the trace key
         * @param traceValue the trace value
         */
        void trace(String traceKey, String traceValue);

        /**
         * Trace with addition.
         *
         * @param traceKey   the trace key
         * @param traceValue the trace value
         */
        void traceAdd(String traceKey, String traceValue);

        /**
         * Message.
         *
         * @param message the message
         */
        void message(String message);
    }
}
