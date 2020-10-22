package com.kevinten.vrml.request.proxy;

import com.kevinten.vrml.core.serialization.Serialization;
import com.kevinten.vrml.error.code.ErrorCodeContext;
import com.kevinten.vrml.error.exception.ErrorCodeException;
import lombok.extern.slf4j.Slf4j;

/**
 * The abstract request proxy.
 *
 * @param <Request>  the request
 * @param <Response> the response
 * @param <Excp>     the exception
 * @param <Code>     the error code
 */
@Slf4j
public abstract class AbstractRequestProxy<Request, Response, Excp extends ErrorCodeException, Code extends ErrorCodeContext> {

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
     * Logging request
     */
    private void beforeInvoke(Request request) {
        log.info("[{}] request[{}]", this.requestName(), Serialization.GSON.toJson(request));
    }

    /**
     * Logging response
     */
    private void afterInvoke(Response response) {
        log.info("[{}] response[{}]", this.requestName(), Serialization.GSON.toJson(response));
    }

    /**
     * Logging response when exception
     *
     * @param response the response
     */
    protected void afterInvokeException(Response response) {
        log.warn("[{}] response[{}]", this.requestName(), Serialization.GSON.toJson(response));
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
            log.warn("[{}] {} is null!", this.requestName(), keyName);
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
            log.warn("[{}] response is null!", this.requestName());
            throw this.throwsError(errorCodes);
        }
    }
}