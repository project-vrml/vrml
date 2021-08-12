package com.kevinten.vrml.switches;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * The Switches configuration.
 */
public interface SwitchesConfiguration {

    /**
     * Gets params as {@code Json} obj.
     *
     * @return the params
     */
    JsonObject getParams();

    /**
     * Check switches.
     *
     * @param switchKeys the switch keys
     * @return the result
     */
    boolean checkSwitches(List<String> switchKeys);
}
