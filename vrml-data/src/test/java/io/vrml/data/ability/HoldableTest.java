package io.vrml.data.ability;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Holdable test.
 */
public class HoldableTest extends TestCase {

    /**
     * Test.
     */
    @Test
    public void test() {
        testFactory();
        testNew();
        testLazyLoad();
    }

    /**
     * Test factory.
     */
    public void testFactory() {
        TestHoldable testHoldable = new TestHoldable();

        testHoldable.holder = Holdable.HolderFactory.supplyHoldData(() -> "str");

        assertEquals("str", testHoldable.holder.toString());
        assertEquals("str", testHoldable.holder.getData());
    }

    /**
     * Test new.
     */
    public void testNew() {
        TestHoldable testHoldable = new TestHoldable();

        testHoldable.holder = new Holdable.Holder<>(() -> "str");

        assertEquals("str", testHoldable.holder.toString());
        assertEquals("str", testHoldable.holder.getData());
    }

    /**
     * Test lazy load.
     */
    public void testLazyLoad() {
        TestHoldable testHoldable = new TestHoldable();

        AtomicInteger i = new AtomicInteger(0);

        testHoldable.holder = Holdable.HolderFactory.supplyHoldData(i::incrementAndGet);

        for (int j = 0; j < 10; j++) {
            assertEquals(1, testHoldable.holder.getData());
        }
    }

    private static class TestHoldable implements Holdable {

        private Holder<Object> holder;
    }
}