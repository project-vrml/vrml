package group.rxcloud.vrml.shutdown;


import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ShutdownHooksTest {

    @org.junit.Test
    public void addShutdownHook() {
        AtomicInteger i = new AtomicInteger();

        ShutdownHooks.addShutdownHook(() -> {
            Thread.sleep(5000);

            i.getAndIncrement();

            int x = i.get();
            System.out.println(Thread.currentThread().getName() + x);
            assertEquals(1, x);
        });

        int x = i.get();
        System.out.println(Thread.currentThread().getName() + x);
        assertEquals(0, x);
    }

    @org.junit.Test
    public void addShutdownHookDaemon() {
        AtomicInteger i = new AtomicInteger();

        ShutdownHooks.setTunedTerminationTimeoutMs(3000);

        ShutdownHooks.addShutdownHookDaemon(() -> {
            Thread.sleep(1000);

            i.getAndIncrement();

            int x = i.get();
            System.out.println(Thread.currentThread().getName() + x);
            assertEquals(1, x);
        });

        ShutdownHooks.addShutdownHookDaemon(() -> {
            Thread.sleep(2000);

            i.getAndIncrement();

            int x = i.get();
            System.out.println(Thread.currentThread().getName() + x);
            assertEquals(2, x);
        });

        ShutdownHooks.addShutdownHookDaemon(() -> {
            Thread.sleep(5000);

            i.getAndIncrement();

            int x = i.get();
            System.out.println(Thread.currentThread().getName() + x);
            assertEquals(3, x);
        });

        int x = i.get();
        System.out.println(Thread.currentThread().getName() + x);
        assertEquals(0, x);
    }
}