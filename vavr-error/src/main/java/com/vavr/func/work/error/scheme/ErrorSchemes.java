package com.vavr.func.work.error.scheme;

/**
 * Application error scheme context
 */
public enum ErrorSchemes implements ErrorSchemeContext {

    /**
     * Error schemes
     */
    Success("Success", "Success!", "Success"),
    ;

    private String schemeName;
    private String schemeDesc;
    private String schemePrompt;

    ErrorSchemes(String schemeName, String schemeDesc, String schemePrompt) {
        this.schemeName = schemeName;
        this.schemeDesc = schemeDesc;
        this.schemePrompt = schemePrompt;
    }

    /**
     * Gets scheme name.
     *
     * @return the scheme name
     */
    @Override
    public String getSchemeName() {
        return schemeName;
    }

    /**
     * Gets scheme desc.
     *
     * @return the scheme desc
     */
    @Override
    public String getSchemeDesc() {
        return schemeDesc;
    }

    /**
     * Gets scheme prompt.
     *
     * @return the scheme prompt
     */
    @Override
    public String getSchemePrompt() {
        return schemePrompt;
    }
}
