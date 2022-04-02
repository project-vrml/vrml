package group.rxcloud.vrml.trace;

import group.rxcloud.vrml.data.ability.Traceable;

/**
 * The Tracer API {@code Traceable}.
 *
 * @param <TraceObj> the obj impl {@code Traceable}.
 */
public interface MapTracer<TraceObj extends Traceable> {

    /**
     * Init obj.
     *
     * @param traceObj the trace obj
     */
    void initObj(TraceObj traceObj);

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
}
