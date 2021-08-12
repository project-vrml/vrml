package com.kevinten.vrml.switches;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The Switches API test.
 */
public class SwitchesTest {

    @Test
    public void runWithSwitch_String() {
        Switches.INS.runWithSwitch("test", () -> System.out.println("switches is true"));
    }

    @Test
    public void callWithSwitch_String() {
        String s = "switches is true";
        String call = Switches.INS.callWithSwitch("test", () -> s);
        assertEquals(s, call);
    }

    @Test
    public void callWithSwitchOrDefault_String() {
        String s1 = "switches is true";
        String s2 = "switches is false";
        String call = Switches.INS.callWithSwitchOrDefault("false", () -> s1, s2);
        assertEquals(s2, call);
    }

    @Test
    public void getSwitch_String() {
        boolean switches = Switches.INS.getSwitch(new Switches.SwitchKey() {
            @Override
            public List<String> getSortedKeys() {
                return Collections.singletonList("test");
            }
        });
        assertTrue(switches);
    }

    /**
     * Gets switch obj.
     */
    @Test
    public void getSwitch_Obj() {
        Switches.configuration = new TestSwitchesConfiguration();

        Switches.SwitchKey switchKey = Switches.SwitchKeyBuilder.builder()
                .next("test1")
                .next("test2")
                .next("test3")
                .build();
        try {
            Switches.INS.getSwitch(switchKey);
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * The Test switches configuration impl.
     */
    @Component
    public static class TestSwitchesConfiguration implements SwitchesConfiguration {

        @Override
        public JsonObject getParams() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("test1", true);
            jsonObject.addProperty("test", true);
            return jsonObject;
        }

        @Override
        public boolean checkSwitches(List<String> switchKeys) {
            if (CollectionUtils.isEmpty(switchKeys)) {
                return false;
            }
            JsonObject params = getParams();
            for (String switchKey : switchKeys) {
                JsonElement jsonElement = params.get(switchKey);
                if (jsonElement != null) {
                    return jsonElement.getAsBoolean();
                }
            }
            return false;
        }
    }
}