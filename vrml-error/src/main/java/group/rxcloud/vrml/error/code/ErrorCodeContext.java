package group.rxcloud.vrml.error.code;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * The interface {@code ErrorCode} context.
 */
public interface ErrorCodeContext extends Serializable {

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
     * Generate the error code.
     */
    interface ErrorCodeGenerator {

        // -- CODE PREFIX

        /**
         * The prefix of {@code parameter} error.
         */
        String PARAMETER_ERROR_PREFIX = "10";

        /**
         * The prefix of {@code business} error.
         */
        String BUSINESS_ERROR_PREFIX = "20";

        /**
         * The prefix of {@code repository service}, Such as DB、Redis or ES.
         */
        String REPOSITORY_ERROR_PREFIX = "30";

        /**
         * The prefix of {@code dependent service}, Such as Http or Third api.
         */
        String DEPENDENT_SERVICE_ERROR_PREFIX = "40";

        /**
         * The prefix of {@code system} error.
         */
        String SYSTEM_ERROR_PREFIX = "50";

        /**
         * The System Codes.
         *
         *  Set a single unique code for your application.
         */
        String applicationErrorCode();

        /**
         * Creates an error code.
         *
         * @param prefix the prefix of error code.
         * @param code   the sub-code.
         * @return the error code.
         */
        default String createErrorCode(String prefix, String code) {
            return applicationErrorCode() + "-" + prefix + code;
        }

        /**
         * Creates an error code because of invalid parameter.
         *
         * @param code the sub-code.
         * @return 10Xxxx
         */
        default String createParameterErrorCode(String code) {
            return createErrorCode(PARAMETER_ERROR_PREFIX, code);
        }

        /**
         * Creates an error code because of failed to call the business process.
         *
         * @param code the sub-code.
         * @return 20Xxxx
         */
        default String createBusinessProcessErrorCode(String code) {
            return createErrorCode(BUSINESS_ERROR_PREFIX, code);
        }

        /**
         * Creates an error code because of invalid repository operation. Such as the operation of DB, Redis or ES.
         *
         * @param code the sub-code.
         * @return 30Xxxx
         */
        default String createRepositoryErrorCode(String code) {
            return createErrorCode(REPOSITORY_ERROR_PREFIX, code);
        }

        /**
         * Creates an error code because of failed to call the dependent services.
         *
         * @param code the sub-code.
         * @return 40Xxxx
         */
        default String createDependentServiceErrorCode(String code) {
            return createErrorCode(DEPENDENT_SERVICE_ERROR_PREFIX, code);
        }

        /**
         * Creates an error code because of unknown system exception.
         *
         * @param code the sub-code.
         * @return 50Xxxx
         */
        default String createSystemErrorCode(String code) {
            return createErrorCode(SYSTEM_ERROR_PREFIX, code);
        }
    }

    /**
     * The {@code ErrorCode} expand functionality manager.
     *
     * @param <Context> the specific context
     */
    interface ErrorCodeManager<Context extends ErrorCodeContext> {

        // -- CHECK DUPLICATED CODE

        /**
         * Assert {@code ErrorCode} not duplicate
         *
         * @param errorContexts the {@code ErrorCode} contexts
         */
        static void assertErrorCodesNoDuplicate(ErrorCodeContext[] errorContexts) {
            Set<String> duplicated = new HashSet<>(errorContexts.length << 1);
            for (ErrorCodeContext value : errorContexts) {
                if (duplicated.contains(value.getCode())) {
                    throw new IllegalArgumentException("ErrorCodes can't be duplicated code[" + value.getCode() + "]");
                } else {
                    duplicated.add(value.getCode());
                }
            }
            duplicated.clear();
        }

        /**
         * Show {@code ErrorCode} information like:
         * <pre>
         *        PARAMETER_ERROR  SEC-10000  Invalid parameter error!
         * </pre>
         *
         * @param errorCodeContext the {@code ErrorCode} context item
         */
        void showErrorCodeItem(Context errorCodeContext);

        /**
         * Show {@code ErrorCode} information like:
         * <pre>
         *        PARAMETER_ERROR  SEC-100000  Invalid parameter error!
         * </pre>
         *
         * @param errorCodeContexts the {@code ErrorCode} contexts
         */
        default void showErrorCodes(Context[] errorCodeContexts) {
            showErrorCodes(errorCodeContexts, this::showErrorCodeItem);
        }

        /**
         * Show ErrorCode.
         *
         * @param errorCodeContexts the {@code ErrorCode} contexts
         * @param showingMsg        the showing msg
         */
        default void showErrorCodes(Context[] errorCodeContexts, Consumer<Context> showingMsg) {
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
