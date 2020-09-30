package io.vrml.intercept.aspect;

import io.vrml.intercept.annotation.ApiLogInterceptor;
import io.vrml.intercept.config.ApiLogConfiguration;
import io.vrml.core.beans.SpringContextConfigurator;
import io.vrml.core.serialization.Serialization;
import io.vrml.log.Logs;
import io.vavr.Lazy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Abstract api log interceptor aspect.
 * If you want to use this aspect around {@link ApiLogInterceptor}.
 * Defined the spring bean with {@link Aspect} and {@link Configuration}
 * which extends {@link AbstractApiLogInterceptorAspect}.
 */
@Aspect
public abstract class AbstractApiLogInterceptorAspect {

    // -- static (thread safe)

    /**
     * Using {@link Logs} to logging the api log.
     */
    protected static Logs defaultLogs = Logs.Factory.getLogs(AbstractApiLogInterceptorAspect.class)
            // Using the default key of {@link Logs} named {@code AbstractApiLogInterceptorAspect}.
            .key(AbstractApiLogInterceptorAspect.class);

    /**
     * key   : className.methodName
     * value : the thread safe {@code Logs} object
     *
     * @see ApiLogInterceptorUtils#generateLogsKey(ProceedingJoinPoint)
     */
    protected static Map<String, Logs> keyLogsMap = new ConcurrentHashMap<>();

    /**
     * Api Log module config.
     */
    protected static Lazy<ApiLogConfiguration> configuration = Lazy.of(() -> SpringContextConfigurator.getBean(ApiLogConfiguration.class))
            // get configuration from spring context,
            // if configuration is null, close all api log.
            .map(AbstractApiLogInterceptorAspect::applyDefaultConfig);

    private static ApiLogConfiguration applyDefaultConfig(ApiLogConfiguration apiLogConfiguration) {
        if (apiLogConfiguration == null) {
            defaultLogs.error("[AbstractApiLogInterceptorAspect.static] ApiLogConfiguration is null! Please supply the configuration bean, otherwise will close all api log.");
            apiLogConfiguration = new ApiLogConfiguration() {

                /* default close all api log. */

                @Override
                public boolean isOpenRequestLog(String logsKey) {
                    return false;
                }

                @Override
                public boolean isOpenResponseLog(String logsKey) {
                    return false;
                }

                @Override
                public boolean isOpenErrorLog(String logsKey) {
                    return false;
                }
            };
        }
        return apiLogConfiguration;
    }

    protected static Logs createKeyLogs(String theKey) {
        return keyLogsMap.computeIfAbsent(theKey, key -> defaultLogs.key(key));
    }

    /**
     * Request invoker proxy.
     *
     * @param pjp the pjp
     * @return the invoke response
     * @throws Throwable the invoke throwable
     */
    @Around("@within(io.vrml.api.intercept.annotation.ApiLogInterceptor)")
    public Object requestInvoker(ProceedingJoinPoint pjp) throws Throwable {
        final String logsKey = ApiLogInterceptorUtils.generateLogsKey(pjp);
        try {
            // whether to do the before process
            boolean execDoBefore = this.isDoBefore(pjp, logsKey);
            if (execDoBefore) {
                // do before process
                this.doBefore(pjp, logsKey);
            }

            // process
            Object proceed = pjp.proceed();

            // whether to do the after process
            boolean execDoAfter = this.isDoAfter(pjp, proceed, logsKey);
            if (execDoAfter) {
                // do after process
                this.doAfter(pjp, proceed, logsKey);
            }

            return proceed;
        } catch (Throwable throwable) {

            // whether to do the exception process
            if (this.isDoException(pjp, throwable, logsKey)) {
                // do exception process
                return this.doException(pjp, throwable, logsKey);
            } else {
                // direct throws
                throw throwable;
            }
        }
    }

    /**
     * Whether to do the {@link #doBefore(ProceedingJoinPoint, String)} process.
     *
     * @param pjp     the pjp
     * @param logsKey the logs key
     * @return {@code true} will exec {@link #doBefore(ProceedingJoinPoint, String)}
     */
    protected boolean isDoBefore(ProceedingJoinPoint pjp, String logsKey) {
        return configuration.get().isOpenRequestLog(logsKey);
    }

    /**
     * Do before.
     *
     * @param pjp     the pjp
     * @param logsKey the logs key
     */
    protected void doBefore(ProceedingJoinPoint pjp, String logsKey) {
        createKeyLogs(logsKey).info("[{}] api request[{}]",
                logsKey,
                Serialization.toJsonSafe(pjp.getArgs()));
    }

    /**
     * Whether to do the {@link #doAfter(ProceedingJoinPoint, Object, String)} process.
     *
     * @param pjp     the pjp
     * @param proceed the process result
     * @param logsKey the logs key
     * @return {@code true} will exec {@link #doAfter(ProceedingJoinPoint, Object, String)}
     */
    protected boolean isDoAfter(ProceedingJoinPoint pjp, Object proceed, String logsKey) {
        return configuration.get().isOpenResponseLog(logsKey);
    }

    /**
     * Do after.
     *
     * @param pjp     the pjp
     * @param proceed the result
     * @param logsKey the logs key
     */
    protected void doAfter(ProceedingJoinPoint pjp, Object proceed, String logsKey) {
        createKeyLogs(logsKey).info("[{}] api response[{}]",
                logsKey,
                Serialization.toJsonSafe(proceed));
    }

    /**
     * whether to do the {@link #doException(ProceedingJoinPoint, Throwable, String)} process.
     *
     * @param pjp       the pjp
     * @param throwable the process throwable
     * @param logsKey   the logs key
     * @return {@code true} will exec {@link #doException(ProceedingJoinPoint, Throwable, String)}
     */
    public boolean isDoException(ProceedingJoinPoint pjp, Throwable throwable, String logsKey) {
        return configuration.get().isOpenErrorLog(logsKey);
    }

    /**
     * Do exception.
     *
     * @param pjp       the pjp
     * @param throwable the process throwable
     * @param logsKey   the logs key
     * @return the process response
     * @throws Throwable the process throwable
     */
    public Object doException(ProceedingJoinPoint pjp, Throwable throwable, String logsKey) throws Throwable {
        createKeyLogs(logsKey).error("[{}] api throwable[{}]",
                logsKey,
                throwable.getMessage(),
                throwable);
        throw throwable;
    }
}

interface ApiLogInterceptorUtils {

    /**
     * Generate {@link Logs} key as className.methodName
     *
     * @param pjp the pjp
     * @return className.methodName
     */
    static String generateLogsKey(ProceedingJoinPoint pjp) {
        return pjp.getTarget().getClass().getSimpleName() + "." + pjp.getSignature().getName();
    }
}
