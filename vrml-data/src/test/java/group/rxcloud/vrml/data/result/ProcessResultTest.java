package group.rxcloud.vrml.data.result;

import io.vavr.Tuple;
import io.vavr.Tuple1;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.control.Either;
import io.vavr.control.Option;
import junit.framework.TestCase;
import org.junit.Test;

import static group.rxcloud.vrml.data.result.ProcessResult.SUCCESS_INSTANCE;

/**
 * The Process result test.
 */
public class ProcessResultTest extends TestCase {

    /**
     * Test of.
     */
    @Test
    public void testOf() {
        // unknown
        Tuple1<Boolean> tuple1 = Tuple.of(null);

        ProcessResult processResult = ProcessResult.of(tuple1);

        assertFalse(processResult.isSuccess());
        assertFalse(processResult.isFailure());
        assertTrue(processResult.isUnknown());

        assertNull(processResult.getErrorMessage());
        assertNull(processResult.get());
    }

    /**
     * Test of 0.
     */
    @Test
    public void testOf0() {
        // unknown
        Tuple2<Boolean, String> tuple2 = Tuple.of(null, "str");

        ProcessResult processResult = ProcessResult.of(tuple2);

        assertFalse(processResult.isSuccess());
        assertFalse(processResult.isFailure());
        assertTrue(processResult.isUnknown());

        assertEquals("str", processResult.getErrorMessage());
        assertNull(processResult.get());
    }

    /**
     * Test of 1.
     */
    @Test
    public void testOf1() {
        // unknown
        Tuple3<Boolean, String, String> tuple3 = Tuple.of(null, "str", "obj");

        ProcessResult processResult = ProcessResult.of(tuple3);

        assertFalse(processResult.isSuccess());
        assertFalse(processResult.isFailure());
        assertTrue(processResult.isUnknown());

        assertEquals("str", processResult.getErrorMessage());
        assertEquals("obj", processResult.get());
    }

    /**
     * Test of 2.
     */
    @Test
    public void testOf2() {
        // success
        Either<String, String> either = Either.right("obj");

        ProcessResult processResult = ProcessResult.of(either);

        assertTrue(processResult.isSuccess());
        assertFalse(processResult.isFailure());
        assertFalse(processResult.isUnknown());

        assertNull(processResult.getErrorMessage());
        assertEquals("obj", processResult.get());
    }

    /**
     * Test of 3.
     */
    @Test
    public void testOf3() {
        // failure
        Option<String> either = Option.none();

        ProcessResult processResult = ProcessResult.of(either);

        assertFalse(processResult.isSuccess());
        assertTrue(processResult.isFailure());
        assertFalse(processResult.isUnknown());

        assertNull(processResult.getErrorMessage());
        assertNull(processResult.get());
    }

    /**
     * Test failure.
     */
    @Test
    public void testFailure() {
        ProcessResult<Void> failure = ProcessResult.failure("str");

        assertTrue(failure.isFailure());
        assertEquals("str", failure.getErrorMessage());
    }

    /**
     * Test failure 1.
     */
    @Test
    public void testFailure1() {
        ProcessResult<String> failure = ProcessResult.failure("str", "obj");

        assertTrue(failure.isFailure());
        assertEquals("str", failure.getErrorMessage());
        assertEquals("obj", failure.get());
    }

    /**
     * Test success.
     */
    @Test
    public void testSuccess() {
        ProcessResult<Void> success = ProcessResult.success();

        assertTrue(success.isSuccess());
        assertEquals(SUCCESS_INSTANCE, success);
    }

    /**
     * Test success 1.
     */
    @Test
    public void testSuccess1() {
        ProcessResult<String> success = ProcessResult.success("obj");

        assertTrue(success.isSuccess());
        assertEquals("obj", success.get());
    }

    /**
     * Test unknown.
     */
    @Test
    public void testUnknown() {
        ProcessResult<Void> unknown = ProcessResult.unknown("str");

        assertTrue(unknown.isUnknown());
        assertEquals("str", unknown.getErrorMessage());
    }

    /**
     * Test unknown 1.
     */
    @Test
    public void testUnknown1() {
        ProcessResult<String> unknown = ProcessResult.unknown("str", "obj");

        assertTrue(unknown.isUnknown());
        assertEquals("str", unknown.getErrorMessage());
        assertEquals("obj", unknown.get());
    }
}