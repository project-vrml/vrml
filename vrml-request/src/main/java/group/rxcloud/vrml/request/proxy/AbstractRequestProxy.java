package group.rxcloud.vrml.request.proxy;

import group.rxcloud.vrml.core.serialization.Serialization;
import group.rxcloud.vrml.error.code.ErrorCodeContext;
import group.rxcloud.vrml.error.exception.ErrorCodeException;
import group.rxcloud.vrml.log.Logs;
import io.vavr.control.Either;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract request proxy.
 *
 * @param <Request>  the request
 * @param <Response> the response
 * @param <Excp>     the exception
 * @param <Code>     the error code
 */
public abstract class AbstractRequestProxy<Request, Response, Excp extends ErrorCodeException, Code extends ErrorCodeContext> {

    /**
     * Request Logs
     */
    protected final Logs logs;

    public AbstractRequestProxy() {
        logs = Logs.Factory.getLogs(this.getClass())
                // use requestName() as log key
                .key(this.requestName());
    }

    /**
     * Request name string.
     *
     * @return the string
     */
    protected abstract String requestName();

    /**
     * Throws error exception.
     *
     * @param errorCodes the error codes
     * @return throws exception
     * @throws Excp the exception
     */
    protected abstract Excp throwsError(Code errorCodes) throws Excp;

    /**
     * Throws error exception.
     *
     * @param errorCodes the error codes
     * @param e          the exception
     * @return throws exception
     * @throws Excp the exception
     */
    protected abstract Excp throwsError(Code errorCodes, Exception e) throws Excp;

    /**
     * Invoke response.
     *
     * @param request the request
     * @return the response
     */
    public Response invoke(Request request) {
        this.beforeInvoke(request);
        try {
            Response response = this.invokeRequest(request);
            this.afterInvoke(response);
            return response;
        } catch (ErrorCodeException e) {
            throw e;
        } catch (Exception e) {
            throw this.throwsError(this.dependentErrorCode(), e);
        }
    }

    /**
     * Invoke response with simple retry.
     *
     * @param request        the request
     * @param invokeMaxCount invoke max count
     * @return the left is errors when all is failure; the right is response when success
     */
    public Either<List<ErrorCodeException>, Response> invokeWithRetry(Request request, int invokeMaxCount) {
        if (invokeMaxCount < 1) {
            throw new IllegalArgumentException(String.format("[%s] invokeWithRetry invokeMaxCount[%s] is illegal.",
                    this.requestName(), invokeMaxCount));
        }

        List<ErrorCodeException> retryErrors = new ArrayList<>();
        for (int i = 0; i < invokeMaxCount; i++) {
            try {
                Response response = this.invoke(request);
                return Either.right(response);
            } catch (ErrorCodeException e) {
                if (logs.isWarnEnabled()) {
                    logs.warn("[{}] invokeWithRetry count[{}] error: ", this.requestName(), i + 1, e);
                }
                retryErrors.add(this.throwsError(this.dependentErrorCode(), e));
            }
        }
        return Either.left(retryErrors);
    }

    /**
     * Logging request
     */
    private void beforeInvoke(Request request) {
        if (logs.isInfoEnabled()) {
            logs.info("[{}] request[{}]", this.requestName(), Serialization.GSON.toJson(request));
        }
    }

    /**
     * Logging response
     */
    private void afterInvoke(Response response) {
        if (logs.isInfoEnabled()) {
            logs.info("[{}] response[{}]", this.requestName(), Serialization.GSON.toJson(response));
        }
    }

    /**
     * Logging response when exception
     *
     * @param response the response
     */
    protected void afterInvokeException(Response response) {
        if (logs.isWarnEnabled()) {
            logs.warn("[{}] response[{}] error", this.requestName(), Serialization.GSON.toJson(response));
        }
    }

    /**
     * Invoke request response.
     *
     * @param request the request
     * @return the response
     * @throws Exception the exception
     */
    protected abstract Response invokeRequest(Request request) throws Exception;

    /**
     * Dependent error code error codes.
     *
     * @return the error codes
     */
    protected abstract Code dependentErrorCode();

    /**
     * Assert not null.
     *
     * @param o       the o
     * @param keyName the key name
     */
    protected void assertNotNull(Object o, String keyName) {
        this.assertNotNull(o, keyName, this.dependentErrorCode());
    }

    /**
     * Assert not null.
     *
     * @param o          the o
     * @param keyName    the key name
     * @param errorCodes the error codes
     */
    protected void assertNotNull(Object o, String keyName, Code errorCodes) {
        if (o == null) {
            logs.warn("[{}] {} is null!", this.requestName(), keyName);
            throw this.throwsError(errorCodes);
        }
    }

    /**
     * Assert response success.
     *
     * @param response the response
     */
    protected void assertResponseNotNull(Response response) {
        this.assertResponseNotNull(response, this.dependentErrorCode());
    }

    /**
     * Assert response success.
     *
     * @param response   the response
     * @param errorCodes the error codes
     */
    protected void assertResponseNotNull(Response response, Code errorCodes) {
        if (response == null) {
            logs.warn("[{}] response is null!", this.requestName());
            throw this.throwsError(errorCodes);
        }
    }
}