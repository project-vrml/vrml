package group.rxcloud.vrml.core.serialization;

/**
 * The Serialization test.
 */
public class SerializationTest {

    private static class Father {

        private String id;
    }

    private static class Child extends Father {

        private String id;

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "id=" + id;
        }
    }

    /**
     * Test.
     */
    public void test() {
        Child child = new Child();
        child.setId("1");

        // Gson.toJson will error and throw exception
        try {
            String json = Serialization.GSON.toJson(child);
        } catch (Exception e) {
            assert e.getClass().equals(IllegalArgumentException.class);
        }

        // Gson.toJson will be replaced by toString when error.
        String jsonSafe = Serialization.toJsonSafe(child);
        assert "id=1".equals(jsonSafe);
    }
}