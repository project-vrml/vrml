package com.kevinten.vrml.switches;


import com.google.gson.JsonObject;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class SwitchesTest {

    @Test
    public void getSwitch() {
        Switches.SwitchKey switchKey = Switches.SwitchKeyBuilder.builder()
                .next("1")
                .next("2")
                .next("3")
                .build();
        boolean switches = Switches.INS.getSwitch(switchKey);
        assertTrue(switches);
    }

    @Test
    public void runWithSwitch() {
    }

    @Test
    public void testRunWithSwitch() {
    }

    @Test
    public void callWithSwitch() {
    }

    @Test
    public void testCallWithSwitch() {
    }

    @Component
    public static class TestSwitchesConfiguration implements SwitchesConfiguration {

        private Map<String, JsonObject> map;

        @Override
        public JsonObject getParams() {
            JsonObject jsonObject = new JsonObject();
            map.forEach(jsonObject::add);
            return jsonObject;
        }
    }
}