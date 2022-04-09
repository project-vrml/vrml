[![Build Status](https://travis-ci.org/vavr-io/vavr-gson.svg?branch=master)](https://travis-ci.org/vavr-io/vavr-gson)

# vrml-core

The core API for all vrml lib for [vrml](https://github.com/kevinten10/vrml) library

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-core</artifactId>
  <version>1.0.4</version>
</dependency>
```

### Beans

Provide a static bean container so that other static APIs can obtain the required beans.

```java
/**
 * The Spring context configurator test.
 */
public class SpringContextConfiguratorTest {

    /**
     * This is a spring app bootstrap class demo
     */
    public static class SpringAppBootstrap implements ApplicationContextAware {

        /**
         * The entry point of application.
         *
         * @param args the input arguments
         */
        public static void main(String[] args) {
            // run spring 
        }

        /**
         * Inject {@code applicationContext} to static spring context container {@link SpringContextConfigurator}
         */
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            SpringContextConfigurator.setStaticApplicationContext(applicationContext);
        }
    }
    
    /**
     * You can get bean from spring context by {@code SpringContextConfigurator}.
     */
    public void getBeans(){
        SpringContextConfigurator.getBean("name");
    }
}
```

### Global Configuration

Provide a global configuration for the Vrml API, such as indicating whether to use the Spring environment.

```java
/**
 * The Vrml test.
 */
public class VrmlTest {

    /**
     * Test.
     */
    public void test() {
        // default use spring
        assert Vrml.isUseSpringConfig();

        // use custom
        Vrml.builder().useCustomConfig().build();
        assert !Vrml.isUseSpringConfig();

        // use spring
        Vrml.builder().useSpringConfig().build();
        assert Vrml.isUseSpringConfig();
    }
}
```

### Safe serialization tool

Serialized parsing is used to print logs in each API. 

In order to avoid the failure of serialized parsing to affect the main process, 

provide a secure serialization tool.
 
After the serialized parsing fails, it will return `toString` method instead of throwing an exception.

```java
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
            assert e.getClass().equals(JsonIOException.class);
        }

        // Gson.toJson will be replaced by toString when error.
        String jsonSafe = Serialization.toJsonSafe(child);
        assert "id=1".equals(jsonSafe);
    }
}
```

## Become a Developer

Developer repository can be found [here](https://github.com/kevinten10/vrml/tree/develop/vrml-core).

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-core</artifactId>
  <version>1.0.4</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.