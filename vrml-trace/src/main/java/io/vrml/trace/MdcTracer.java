package io.vrml.trace;

/**
 * The Tracer API with {@code MDC}.
 */
public interface MdcTracer {

    /**
     * Init mdc.
     *
     * @param mdcKey   the mdc key
     * @param mdcValue the mdc value
     */
    void initMdc(String mdcKey, String mdcValue);

    /**
     * Clear mdc.
     */
    void clearMdc();

    /**
     * Put mdc.
     *
     * @param mdcKey   the mdc key
     * @param mdcValue the mdc value
     */
    void put(String mdcKey, String mdcValue);

    /**
     * Remove mdc.
     *
     * @param mdcKey the mdc key
     */
    void remove(String mdcKey);
}
