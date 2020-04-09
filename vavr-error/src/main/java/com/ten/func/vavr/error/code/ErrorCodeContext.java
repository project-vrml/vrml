package com.ten.func.vavr.error.code;

import com.ten.func.vavr.error.scheme.ErrorSchemeContext;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Application error code context
 */
public interface ErrorCodeContext {

    /**
     * The System Code.
     *
     * @apiNote Set a unique code for your application
     */
    String MARKET_E2C_APPLICATION_CODE = "";

    /**
     * Enum name.
     *
     * @return the name
     */
    String name();

    /**
     * Gets code.
     *
     * @return the code
     */
    String getCode();

    /**
     * Gets message.
     *
     * @return the message
     */
    String getMessage();

    /**
     * Gets scheme.
     *
     * @return the scheme
     */
    ErrorSchemeContext getScheme();

    // -- CODE PREFIX

    /**
     * The prefix of {@code parameter} error.
     */
    String PARAMETER_ERROR_PREFIX = "1" + MARKET_E2C_APPLICATION_CODE;

    /**
     * The prefix of {@code business} error.
     */
    String BUSINESS_ERROR_PREFIX = "2" + MARKET_E2C_APPLICATION_CODE;

    /**
     * The prefix of {@code repository service}, Such as DB、Redis or ES.
     */
    String REPOSITORY_ERROR_PREFIX = "3" + MARKET_E2C_APPLICATION_CODE;

    /**
     * The prefix of {@code dependent service}, Such as SOA or Third api.
     */
    String DEPENDENT_SERVICE_ERROR_PREFIX = "4" + MARKET_E2C_APPLICATION_CODE;

    /**
     * The prefix of {@code system} error.
     */
    String SYSTEM_ERROR_PREFIX = "5" + MARKET_E2C_APPLICATION_CODE;

    /**
     * Generate the error code.
     */
    abstract class ErrorCodeGenerator {

        /**
         * Creates an error code.
         *
         * @param prefix the prefix of error code.
         * @param code   the sub-code.
         * @return the error code.
         */
        private static String createErrorCode(String prefix, String code) {
            return prefix + code;
        }

        /**
         * Creates an error code because of invalid parameter.
         *
         * @param code the sub-code.
         * @return 10xxx string
         */
        static String createParameterErrorCode(String code) {
            return createErrorCode(PARAMETER_ERROR_PREFIX, code);
        }

        /**
         * Creates an error code because of failed to call the business process.
         *
         * @param code the sub-code.
         * @return 20xxx string
         */
        static String createBusinessProcessErrorCode(String code) {
            return createErrorCode(BUSINESS_ERROR_PREFIX, code);
        }

        /**
         * Creates an error code because of invalid repository operation. Such as the operation of DB, Redis or ES.
         *
         * @param code the sub-code.
         * @return 30xxx string
         */
        static String createRepositoryErrorCode(String code) {
            return createErrorCode(REPOSITORY_ERROR_PREFIX, code);
        }

        /**
         * Creates an error code because of failed to call the dependent services.
         *
         * @param code the sub-code.
         * @return 40xxx string
         */
        static String createDependentServiceErrorCode(String code) {
            return createErrorCode(DEPENDENT_SERVICE_ERROR_PREFIX, code);
        }

        /**
         * Creates an error code because of unknown system exception.
         *
         * @param code the sub-code.
         * @return 50xxx string
         */
        static String createSystemErrorCode(String code) {
            return createErrorCode(SYSTEM_ERROR_PREFIX, code);
        }
    }

    /**
     * The error code manager.
     */
    abstract class ErrorCodeManager {

        // -- CHECK DUPLICATED CODE

        /**
         * Assert error codes not duplicate
         *
         * @param errorContexts the error contexts
         */
        static void assertErrorCodesNoDuplicate(ErrorCodeContext[] errorContexts) {
            Set<String> duplicated = new HashSet<>(errorContexts.length << 1);
            for (ErrorCodeContext value : errorContexts) {
                if (duplicated.contains(value.getCode())) {
                    throw new IllegalArgumentException("ErrorCodes can't be duplicated code");
                } else {
                    duplicated.add(value.getCode());
                }
            }
            duplicated.clear();
        }

        /**
         * Show error codes information like:
         * <pre>
         *        PARAMETER_ERROR  1E000  Invalid parameter error!
         * </pre>
         *
         * @param errorCodeContexts the error contexts
         */
        static void showErrorCodes(ErrorCodeContext[] errorCodeContexts) {
            showErrorCodes(errorCodeContexts, errorCodeContext -> {
                System.out.printf("%70s  %5s  %s", errorCodeContext.name(), errorCodeContext.getCode(), errorCodeContext.getMessage());
            });
        }

        static void showErrorCodes(ErrorCodeContext[] errorCodeContexts, Consumer<ErrorCodeContext> showingMsg) {
            Set<String> prefixSet = new HashSet<>(8);
            Arrays.stream(errorCodeContexts)
                    .sorted(Comparator.comparing(ErrorCodeContext::getCode))
                    .peek(value -> {
                        String prefix = value.getCode().substring(0, 1);
                        if (!prefixSet.contains(prefix)) {
                            prefixSet.add(prefix);
                            System.out.println();
                        }
                    })
                    .forEach(value -> {
                        showingMsg.accept(value);
                        System.out.println();
                    });
        }
    }
}
