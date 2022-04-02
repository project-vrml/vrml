package group.rxcloud.vrml.trace;

import org.slf4j.MDC;

/**
 * The Traces API with {@code MDC}.
 */
public abstract class MdcTraces {

    private static final MdcThreadLocalTracer INSTANCE = new MdcThreadLocalTracer();

    /**
     * Use threadLocal {@code MDC} tracer.
     *
     * @return the threadLocal {@code MDC} tracer
     */
    public static MdcThreadLocalTracer useThreadLocal() {
        return INSTANCE;
    }

    /**
     * The threadLocal {@code MDC} tracer.
     */
    public static final class MdcThreadLocalTracer implements MdcTracer {

        // -- INIT

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

        // -- CLEAR

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
    }
}
