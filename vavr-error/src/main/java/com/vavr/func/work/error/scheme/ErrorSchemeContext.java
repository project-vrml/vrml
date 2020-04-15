package com.vavr.func.work.error.scheme;

/**
 * Application error scheme context
 */
public interface ErrorSchemeContext {

    /**
     * Gets scheme name.
     *
     * @return the scheme name
     */
    String getSchemeName();

    /**
     * Gets scheme desc.
     *
     * @return the scheme desc
     */
    String getSchemeDesc();

    /**
     * Gets scheme prompt.
     *
     * @return the scheme prompt
     */
    String getSchemePrompt();
}
