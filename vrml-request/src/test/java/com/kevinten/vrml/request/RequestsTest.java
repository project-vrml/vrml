package com.kevinten.vrml.request;

import com.kevinten.vrml.request.config.RequestConfiguration;
import io.vavr.CheckedRunnable;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Requests test.
 */
public class RequestsTest extends TestCase {

    /**
     * Test of 0.
     */
    @Test
    public void testOf0() {
        // success
        Requests<String> value = Requests.of("value");
        assertEquals("value", value.get());
    }

    /**
     * Test of 1.
     */
    @Test
    public void testOf1() {
        // success
        Requests<String> value = Requests.of(() -> "value");
        assertEquals("value", value.get());

        // failure
        Requests<Object> failure = Requests.of(() -> {
            throw new NullPointerException();
        });
        assertEquals(NullPointerException.class, failure.getCause().getClass());
    }

    /**
     * Test of 2.
     */
    @Test
    public void testOf2() {
        // success
        Requests<Void> success = Requests.of((CheckedRunnable) System.out::println);
        assertTrue(success.isSuccess());

        // failure
        Requests<Void> failure = Requests.of(() -> {
            throw new NullPointerException();
        });
        assertTrue(failure.isFailure());
    }

    /**
     * Test on failure.
     */
    @Test
    public void testOnFailure() {
        AtomicInteger i = new AtomicInteger(0);

        // success will not exec
        Requests<String> success = Requests.of(() -> "value");
        success.onFailure(throwable -> i.incrementAndGet());
        assertEquals(0, i.get());

        // failure will exec
        Requests<Object> failure = Requests.of(() -> {
            throw new NullPointerException();
        });
        failure.onFailure(throwable -> i.incrementAndGet());
        assertEquals(1, i.get());
    }

    /**
     * Test on success.
     */
    @Test
    public void testOnSuccess() {
        AtomicInteger i = new AtomicInteger(0);

        // success will exec
        Requests<String> success = Requests.of(() -> "value");
        success.onSuccess(throwable -> i.incrementAndGet());
        assertEquals(1, i.get());

        // failure will not exec
        Requests<Object> failure = Requests.of(() -> {
            throw new NullPointerException();
        });
        failure.onSuccess(throwable -> i.incrementAndGet());
        assertEquals(1, i.get());
    }

    /**
     * Test record.
     */
    @Test
    public void testRecord() {
        // success
        Requests<String> success = Requests.of(() -> "value");
        try {
            success.record(s -> new RequestConfiguration.RequestReportValue
                    .ReportBuilder("default")
                    .recordValue(s)
                    // success will exec
                    .useConfig()
                    .build());
        } catch (NullPointerException e) {
            // ignore
        }

        // failure
        Requests<Object> failure = Requests.of(() -> {
            throw new NullPointerException();
        });
        failure.record(s -> new RequestConfiguration.RequestReportValue
                .ReportBuilder("default")
                .recordValue(s.toString())
                // failure will not exec
                .useConfig()
                .build());
    }

    /**
     * Test recover.
     */
    @Test
    public void testRecover() {
        // success will not exec
        Requests<String> success = Requests.of(() -> "value");
        Requests<String> recover1 = success.recover(throwable -> "value2");
        assertEquals(success, recover1);

        // failure will exec
        Requests<Object> failure = Requests.of(() -> {
            throw new NullPointerException();
        });
        Requests<Object> recover2 = failure.recover(throwable -> "value2");
        assertNotSame(failure, recover2);
        assertEquals("value2", recover2.get());
    }

    /**
     * Test check.
     */
    @Test
    public void testCheck() {
        // success will exec
        Requests<String> success = Requests.of(() -> "value");
        Requests<String> check1 = success.check(s -> {
            if ("value".equals(s)) {
                throw new NullPointerException();
            }
        });
        assertNotSame(success, check1);
        assertEquals(NullPointerException.class, check1.getCause().getClass());

        // failure will not exec
        Requests<Object> failure = Requests.of(() -> {
            throw new NullPointerException();
        });
        Requests<Object> check2 = failure.check(o -> {
            throw new IllegalArgumentException();
        });
        assertEquals(failure, check2);
        assertEquals(NullPointerException.class, check2.getCause().getClass());
    }

    /**
     * Test or else 1.
     */
    @Test
    public void testOrElse1() {
        // success will not exec
        Requests<String> success = Requests.of(() -> "value");
        Requests<String> orElse1 = success.orElse(Requests.of("value2"));
        assertEquals(success, orElse1);

        // failure will exec
        Requests<Object> failure = Requests.of(() -> {
            throw new NullPointerException();
        });
        Requests<Object> orElse2 = failure.orElse(Requests.of("value"));
        assertNotSame(failure, orElse2);
        assertEquals("value", orElse2.get());
    }

    /**
     * Test or else 2.
     */
    @Test
    public void testOrElse2() {
        // success will not exec
        Requests<String> success = Requests.of(() -> "value");
        Requests<String> orElse1 = success.orElse(() -> Requests.of("value2"));
        assertEquals(success, orElse1);

        // failure will exec
        Requests<Object> failure = Requests.of(() -> {
            throw new NullPointerException();
        });
        Requests<Object> orElse2 = failure.orElse(() -> Requests.of("value"));
        assertNotSame(failure, orElse2);
        assertEquals("value", orElse2.get());
    }

    /**
     * Test get or else get.
     */
    @Test
    public void testGetOrElseGet() {
        // success will not exec
        Requests<String> success = Requests.of(() -> "value");
        String orElseGet1 = success.getOrElseGet((throwable) -> "value2");
        assertEquals(success.get(), orElseGet1);

        // failure will exec
        Requests<Object> failure = Requests.of(() -> {
            throw new NullPointerException();
        });
        Object orElseGet2 = failure.getOrElseGet((throwable) -> "value");
        assertEquals("value", orElseGet2);
    }

    /**
     * Test or else run.
     */
    @Test
    public void testOrElseRun() {
        AtomicInteger i = new AtomicInteger(0);

        // success will not exec
        Requests<String> success = Requests.of(() -> "value");
        success.orElseRun(throwable -> i.incrementAndGet());
        assertEquals(0, i.get());

        // failure will exec
        Requests<Object> failure = Requests.of(() -> {
            throw new NullPointerException();
        });
        failure.orElseRun(throwable -> i.incrementAndGet());
        assertEquals(1, i.get());
    }

    /**
     * Test get or else throw.
     */
    @Test
    public void testGetOrElseThrow() {
        // success will not exec
        Requests<String> success = Requests.of(() -> "value");
        success.getOrElseThrow(throwable -> {
            throw new IllegalArgumentException();
        });
        assertEquals("value", success.get());

        // failure will exec
        Requests<Object> failure = Requests.of(() -> {
            throw new NullPointerException();
        });
        try {
            failure.getOrElseThrow(throwable -> {
                throw new IllegalArgumentException();
            });
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }
}