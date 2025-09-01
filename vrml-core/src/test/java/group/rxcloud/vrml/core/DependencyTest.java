package group.rxcloud.vrml.core;

import org.junit.Test;

/**
 * 依赖测试类
 * 用于验证可选依赖是否可以正常编译
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class DependencyTest {
    
    @Test
    public void testOptionalDependencies() {
        // 测试vrml-metric模块
        try {
            Class<?> metricsClass = Class.forName("group.rxcloud.vrml.metric.Metrics");
            System.out.println("vrml-metric module is available: " + metricsClass.getName());
        } catch (ClassNotFoundException e) {
            System.out.println("vrml-metric module is not available (this is expected in some environments)");
        }
        
        // 测试vrml-log模块
        try {
            Class<?> logsClass = Class.forName("group.rxcloud.vrml.log.Logs");
            System.out.println("vrml-log module is available: " + logsClass.getName());
        } catch (ClassNotFoundException e) {
            System.out.println("vrml-log module is not available (this is expected in some environments)");
        }
        
        // 测试vrml-trace模块
        try {
            Class<?> tracesClass = Class.forName("group.rxcloud.vrml.trace.Traces");
            System.out.println("vrml-trace module is available: " + tracesClass.getName());
        } catch (ClassNotFoundException e) {
            System.out.println("vrml-trace module is not available (this is expected in some environments)");
        }
        
        // 测试vrml-alert模块
        try {
            Class<?> alertsClass = Class.forName("group.rxcloud.vrml.alert.Alerts");
            System.out.println("vrml-alert module is available: " + alertsClass.getName());
        } catch (ClassNotFoundException e) {
            System.out.println("vrml-alert module is not available (this is expected in some environments)");
        }
    }
    
    @Test
    public void testBasicDependencies() {
        // 测试基础依赖
        try {
            // 测试vavr
            io.vavr.control.Try<String> tryResult = io.vavr.control.Try.success("test");
            System.out.println("vavr is available: " + tryResult.get());
            
            // 测试slf4j
            org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DependencyTest.class);
            logger.info("slf4j is available");
            
            // 测试gson
            com.google.gson.Gson gson = new com.google.gson.Gson();
            String json = gson.toJson("test");
            System.out.println("gson is available: " + json);
            
        } catch (Exception e) {
            System.err.println("Basic dependency test failed: " + e.getMessage());
        }
    }
}