package group.rxcloud.vrml.resource;

import io.vavr.control.Try;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class ResourcesTest {

    @org.junit.Test
    public void loadResourcesProperties() {
        Try<Properties> aTry = Resources.loadResourcesProperties("/vrml-test.properties");
        Properties properties = aTry.get();
        String value1 = properties.getProperty("key1");
        assertEquals("value1", value1);
    }

    @org.junit.Test
    public void loadResources() {
        Try<TestJson> aTry = Resources.loadResources("/vrml-test.json", TestJson.class);
        TestJson testJson = aTry.get();
        String value1 = testJson.getKey();
        assertEquals("value1", value1);
    }

    @org.junit.Test
    public void loadFile() {
        Try<TestJson> aTry = Resources.loadFile("./src/test/resources/vrml-test.json", TestJson.class);
        TestJson testJson = aTry.get();
        String value1 = testJson.getKey();
        assertEquals("value1", value1);
    }
}