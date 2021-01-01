package com.kevinten.vrml.error.code;

/**
 * The Default error code context.
 */
public interface DefaultErrorCodeContext extends ErrorCodeContext {

    /**
     * The DefaultErrorCode GENERATOR.
     */
    DefaultErrorCodeGenerator GENERATOR = new DefaultErrorCodeGenerator();
    /**
     * The DefaultErrorCode MANAGER.
     */
    DefaultErrorCodeManager MANAGER = new DefaultErrorCodeManager();

    /**
     * The Default error code generator.
     */
    class DefaultErrorCodeGenerator implements ErrorCodeGenerator {

        /**
         * Default system code.
         *
         * @apiNote Set a single unique code for your application.
         */
        private static final String DEFAULT_APPLICATION_CODE = "DEC";

        @Override
        public String applicationErrorCode() {
            return DEFAULT_APPLICATION_CODE;
        }
    }

    /**
     * The Default error code manager.
     */
    class DefaultErrorCodeManager implements ErrorCodeManager<DefaultErrorCodeContext> {

        @Override
        public void showErrorCodeItem(DefaultErrorCodeContext errorCodeContext) {
            System.out.printf("%70s  %10s  %s", errorCodeContext.name(), errorCodeContext.getCode(), errorCodeContext.getMessage());
        }
    }
}
