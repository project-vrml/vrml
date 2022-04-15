package group.rxcloud.vrml.trace;

import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.MDC;

/**
 * The Mdc traces test.
 */
public class MdcTracesTest extends TestCase {

    /**
     * Test.
     */
    @Test
    public void test() {
        testInitMdc();

        testPut();

        testRemove();

        testClear();
    }

    /**
     * Test init mdc.
     */
    public void testInitMdc() {
        MdcTraces.useThreadLocal().initMdc("test", "test");
        assertEquals("test", MDC.get("test"));
    }

    /**
     * Test put.
     */
    public void testPut() {
        MdcTraces.useThreadLocal().put("test2", "test2");
        assertEquals("test2", MDC.get("test2"));
    }

    /**
     * Test remove.
     */
    public void testRemove() {
        MdcTraces.useThreadLocal().remove("test");
        assertNull(MDC.get("test"));
    }

    /**
     * Test clear.
     */
    public void testClear() {
        MdcTraces.useThreadLocal().clearMdc();
        assertNull(MDC.get("test2"));
    }
}