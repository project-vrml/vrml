package group.rxcloud.vrml.switches;


import com.google.gson.JsonObject;
import org.junit.Test;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

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

        {
            Switches.SwitchKey switchKey1 = Switches.SwitchKeyBuilder.builder()
                    .next("test1")
                    .build();

            boolean value1 = Switches.INS.getSwitch(switchKey1);
            assertTrue(value1);
        }
        {
            Switches.SwitchKey switchKey1 = Switches.SwitchKeyBuilder.builder()
                    .next("test2")
                    .next("test3")
                    .next("test4")
                    .build();

            boolean value1 = Switches.INS.getSwitch(switchKey1);
            assertTrue(value1);
        }
        {
            Switches.SwitchKey switchKey1 = Switches.SwitchKeyBuilder.builder()
                    .next("test2")
                    .build();

            boolean value1 = Switches.INS.getSwitch(switchKey1);
            assertFalse(value1);
        }
        {
            Switches.SwitchKey switchKey1 = Switches.SwitchKeyBuilder.builder()
                    .next("test2")
                    .next("test3")
                    .build();

            boolean value1 = Switches.INS.getSwitch(switchKey1);
            assertFalse(value1);
        }
        {
            Switches.SwitchKey switchKey1 = Switches.SwitchKeyBuilder.builder()
                    .next("test5")
                    .next("test6")
                    .build();

            boolean value1 = Switches.INS.getSwitch(switchKey1);
            assertFalse(value1);
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
            JsonObject value3 = new JsonObject();
            value3.addProperty("test4", true);
            JsonObject value2 = new JsonObject();
            value2.add("test3", value3);
            jsonObject.add("test2", value2);
            return jsonObject;
        }
    }
}