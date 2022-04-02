package group.rxcloud.vrml.trace;

import group.rxcloud.vrml.data.ability.Traceable;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * The Map traces test.
 */
public class MapTracesTest extends TestCase {

    private final TestTraceable testTraceable = new TestTraceable();

    /**
     * Test.
     */
    @Test
    public void test() {
        testInitObj();

        testTrace();

        testTraceAdd();

        testClear();
    }

    /**
     * Test init obj.
     */
    public void testInitObj() {
        MapTraces.useThreadLocal().initObj(testTraceable);
        assertEquals(testTraceable, MapTraces.useThreadLocal().get());
    }

    /**
     * Test trace.
     */
    public void testTrace() {
        MapTraces.useThreadLocal().initObj(testTraceable);
        MapTraces.useThreadLocal().trace("test", "test");
        assertEquals("test", MapTraces.useThreadLocal().get().getTraceMap().get("test"));
    }

    /**
     * Test trace add.
     */
    public void testTraceAdd() {
        MapTraces.useThreadLocal().initObj(testTraceable);
        MapTraces.useThreadLocal().traceAdd("test2", "test2");
        assertEquals(",test2", MapTraces.useThreadLocal().get().getTraceMap().get("test2"));
    }

    /**
     * Test clear.
     */
    public void testClear() {
        MapTraces.useThreadLocal().clear();
        assertNull(MapTraces.useThreadLocal().get());
    }

    /**
     * The Test traceable.
     */
    private static class TestTraceable implements Traceable {

        private Map<String, String> traceMap = new HashMap<>();

        @Override
        public Map<String, String> getTraceMap() {
            return traceMap;
        }

        @Override
        public void setTraceMap(Map<String, String> traceMap) {
            this.traceMap = traceMap;
        }

        @Override
        public void addTrace(String key, String value) {
            traceMap.put(key, value);
        }
    }
}