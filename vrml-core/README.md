# VRML-Core 核心模块

VRML的核心模块，提供统一的API抽象、SPI机制和集成管理功能。

## 功能特性

### 🎯 核心API抽象
- **VrmlApi**: 所有VRML模块API的标记接口
- **VrmlOperations**: 统一的操作接口，支持监控、链路追踪和错误处理
- **VrmlConfiguration**: 统一的配置接口
- **AbstractVrmlOperations**: 抽象基类，提供通用的集成功能

### 🔌 SPI机制
- **VrmlProvider**: 服务提供者接口，支持可插拔的适配器
- **VrmlProviderRegistry**: 服务提供者注册表，支持自动发现和手动注册
- 支持优先级排序和健康检查

### 🔧 集成管理
- **VrmlIntegrationManager**: 统一管理各模块的集成状态
- **VrmlMetricIntegration**: 监控指标集成
- **VrmlLogIntegration**: 日志集成
- **VrmlAlertIntegration**: 告警集成
- **VrmlTraceIntegration**: 链路追踪集成

### ⚙️ 配置管理
- **VrmlConfigurationManager**: 统一的配置管理器
- 支持多种配置源：配置提供者、系统属性、环境变量
- 支持类型转换和缓存

### 🏗️ Spring集成
- **SpringContextConfigurator**: Spring上下文静态访问器
- 支持Bean的获取和管理

## Usage

### Maven

```xml
<dependency>
  <groupId>group.rxcloud</groupId>
  <artifactId>vrml-core</artifactId>
  <version>1.1.4</version>
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
  <version>1.1.4</version>
</dependency>
```

### Contributing

A small number of users have reported problems building vrml. Read our contribution guide for details.