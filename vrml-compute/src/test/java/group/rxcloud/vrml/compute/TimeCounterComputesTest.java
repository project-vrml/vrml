package group.rxcloud.vrml.compute;


import group.rxcloud.vrml.core.tags.Todo;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

@Todo
public class TimeCounterComputesTest {

    @Test
    public void compute_Left() {
        Computes.TC.configuration = new TimeCounterComputes.TimeCounterComputeConfiguration() {
            @Override
            public TimeCounterComputes.TimeCounterComputeConfig getComputeConfiguration(String key) {
                TimeCounterComputes.TimeCounterComputeConfig timeCounterComputeConfig = new TimeCounterComputes.TimeCounterComputeConfig();
                timeCounterComputeConfig.setKey("test1");
                timeCounterComputeConfig.setExpirationTime(10L);
                timeCounterComputeConfig.setTriggerCount(100L);
                return timeCounterComputeConfig;
            }
        };
        {
            // init
            Computes.TC.compute("test1",
                    () -> System.out.println("f1"),
                    () -> System.out.println("f2"));
        }
        // trigger
        AtomicInteger j = new AtomicInteger();
        for (int i = 0; i < 99; i++) {
            Computes.TC.compute("test1",
                    j::getAndIncrement,
                    () -> {
                        throw new RuntimeException("false");
                    });
        }
        assertEquals(99, j.get());
    }

    @Test
    public void compute_Left_TimeOut() {
        Computes.TC.configuration = new TimeCounterComputes.TimeCounterComputeConfiguration() {
            @Override
            public TimeCounterComputes.TimeCounterComputeConfig getComputeConfiguration(String key) {
                TimeCounterComputes.TimeCounterComputeConfig timeCounterComputeConfig = new TimeCounterComputes.TimeCounterComputeConfig();
                timeCounterComputeConfig.setKey("test2");
                timeCounterComputeConfig.setExpirationTime(1L);
                timeCounterComputeConfig.setTriggerCount(10L);
                return timeCounterComputeConfig;
            }
        };
        {
            // init
            Computes.TC.compute("test2",
                    () -> System.out.println("f1"),
                    () -> System.out.println("f2"));
        }
        {
            // trigger
            AtomicInteger j = new AtomicInteger();
            for (int i = 0; i < 10; i++) {
                Computes.TC.compute("test2",
                        () -> {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                            }
                        },
                        j::getAndIncrement);
            }
            assertEquals(1, j.get());
        }
        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
        }
        {
            // trigger
            AtomicInteger j = new AtomicInteger();
            for (int i = 0; i < 10; i++) {
                Computes.TC.compute("test2",
                        () -> {
                            try {
                                Thread.sleep(1100);
                            } catch (InterruptedException e) {
                            }
                        },
                        j::getAndIncrement);
            }
            assertEquals(0, j.get());
        }
    }

    @Test
    public void compute_Right() {
        Computes.TC.configuration = new TimeCounterComputes.TimeCounterComputeConfiguration() {
            @Override
            public TimeCounterComputes.TimeCounterComputeConfig getComputeConfiguration(String key) {
                TimeCounterComputes.TimeCounterComputeConfig timeCounterComputeConfig = new TimeCounterComputes.TimeCounterComputeConfig();
                timeCounterComputeConfig.setKey("test3");
                timeCounterComputeConfig.setExpirationTime(10L);
                timeCounterComputeConfig.setTriggerCount(1L);
                return timeCounterComputeConfig;
            }
        };
        {
            // init
            Computes.TC.compute("test3",
                    () -> System.out.println("f1"),
                    () -> System.out.println("f2"));
        }
        // trigger
        AtomicInteger j = new AtomicInteger();
        for (int i = 0; i < 10; i++) {
            Computes.TC.compute("test3",
                    () -> {
                        throw new RuntimeException("false");
                    },
                    j::getAndIncrement);
        }
        assertEquals(10, j.get());
    }

    @Test
    public void compute_LeftAndRight() {
        Computes.TC.configuration = new TimeCounterComputes.TimeCounterComputeConfiguration() {
            @Override
            public TimeCounterComputes.TimeCounterComputeConfig getComputeConfiguration(String key) {
                TimeCounterComputes.TimeCounterComputeConfig timeCounterComputeConfig = new TimeCounterComputes.TimeCounterComputeConfig();
                timeCounterComputeConfig.setKey("test4");
                timeCounterComputeConfig.setExpirationTime(10L);
                timeCounterComputeConfig.setTriggerCount(2L);
                return timeCounterComputeConfig;
            }
        };
        {
            // init
            Computes.TC.compute("test4",
                    () -> System.out.println("f1"),
                    () -> System.out.println("f2"));
        }
        // trigger
        AtomicInteger j = new AtomicInteger();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                Computes.TC.compute("test4",
                        j::getAndIncrement,
                        () -> {
                            throw new RuntimeException("false");
                        });
            } else {
                Computes.TC.compute("test4",
                        () -> {
                            throw new RuntimeException("false");
                        },
                        j::getAndIncrement);
            }
        }
        assertEquals(10, j.get());
    }
}