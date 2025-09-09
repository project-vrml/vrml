package group.rxcloud.vrml.cache.monitoring;

import group.rxcloud.vrml.cache.api.CacheOperations;
import group.rxcloud.vrml.cache.api.CacheStats;
import group.rxcloud.vrml.core.integration.VrmlAlertIntegration;
import group.rxcloud.vrml.core.integration.VrmlMetricIntegration;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 缓存监控组件
 * 提供缓存性能监控、告警和统计功能
 * 
 * @author VRML Team
 * @since 1.2.0
 */
public class CacheMonitor {
    
    private static final Logger log = LoggerFactory.getLogger(CacheMonitor.class);
    
    /**
     * 监控配置
     */
    private final MonitorConfig config;
    
    /**
     * 缓存操作实例映射
     */
    private final ConcurrentMap<String, CacheOperations> cacheInstances = new ConcurrentHashMap<>();
    
    /**
     * 性能指标收集器
     */
    private final ConcurrentMap<String, PerformanceMetrics> metricsCollectors = new ConcurrentHashMap<>();
    
    /**
     * 定时任务执行器
     */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2, 
        r -> {
            Thread t = new Thread(r, "vrml-cache-monitor-" + System.currentTimeMillis());
            t.setDaemon(true);
            return t;
        });
    
    /**
     * 构造函数
     * 
     * @param config 监控配置
     */
    public CacheMonitor(MonitorConfig config) {
        this.config = config;
        startMonitoring();
    }
    
    /**
     * 监控配置
     */
    public static class MonitorConfig {
        private boolean enabled = true;
        private Duration monitorInterval = Duration.ofMinutes(1);
        private Duration alertInterval = Duration.ofMinutes(5);
        private double hitRateThreshold = 0.8;
        private double errorRateThreshold = 0.05;
        private long responseTimeThreshold = 1000; // ms
        private boolean enablePerformanceMetrics = true;
        private boolean enableHealthCheck = true;
        private boolean enableAlerts = true;
        
        // Getters and setters
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
        
        public Duration getMonitorInterval() { return monitorInterval; }
        public void setMonitorInterval(Duration monitorInterval) { this.monitorInterval = monitorInterval; }
        
        public Duration getAlertInterval() { return alertInterval; }
        public void setAlertInterval(Duration alertInterval) { this.alertInterval = alertInterval; }
        
        public double getHitRateThreshold() { return hitRateThreshold; }
        public void setHitRateThreshold(double hitRateThreshold) { this.hitRateThreshold = hitRateThreshold; }
        
        public double getErrorRateThreshold() { return errorRateThreshold; }
        public void setErrorRateThreshold(double errorRateThreshold) { this.errorRateThreshold = errorRateThreshold; }
        
        public long getResponseTimeThreshold() { return responseTimeThreshold; }
        public void setResponseTimeThreshold(long responseTimeThreshold) { this.responseTimeThreshold = responseTimeThreshold; }
        
        public boolean isEnablePerformanceMetrics() { return enablePerformanceMetrics; }
        public void setEnablePerformanceMetrics(boolean enablePerformanceMetrics) { this.enablePerformanceMetrics = enablePerformanceMetrics; }
        
        public boolean isEnableHealthCheck() { return enableHealthCheck; }
        public void setEnableHealthCheck(boolean enableHealthCheck) { this.enableHealthCheck = enableHealthCheck; }
        
        public boolean isEnableAlerts() { return enableAlerts; }
        public void setEnableAlerts(boolean enableAlerts) { this.enableAlerts = enableAlerts; }
    }
    
    /**
     * 性能指标收集器
     */
    private static class PerformanceMetrics {
        private final AtomicLong requestCount = new AtomicLong(0);
        private final AtomicLong errorCount = new AtomicLong(0);
        private final AtomicLong totalResponseTime = new AtomicLong(0);
        private final AtomicLong maxResponseTime = new AtomicLong(0);
        private volatile long lastResetTime = System.currentTimeMillis();
        
        public void recordRequest(long responseTime, boolean success) {
            requestCount.incrementAndGet();
            if (!success) {
                errorCount.incrementAndGet();
            }
            
            totalResponseTime.addAndGet(responseTime);
            
            // 更新最大响应时间
            long currentMax = maxResponseTime.get();
            while (responseTime > currentMax && !maxResponseTime.compareAndSet(currentMax, responseTime)) {
                currentMax = maxResponseTime.get();
            }
        }
        
        public double getErrorRate() {
            long requests = requestCount.get();
            return requests == 0 ? 0.0 : (double) errorCount.get() / requests;
        }
        
        public double getAverageResponseTime() {
            long requests = requestCount.get();
            return requests == 0 ? 0.0 : (double) totalResponseTime.get() / requests;
        }
        
        public long getMaxResponseTime() {
            return maxResponseTime.get();
        }
        
        public long getRequestCount() {
            return requestCount.get();
        }
        
        public void reset() {
            requestCount.set(0);
            errorCount.set(0);
            totalResponseTime.set(0);
            maxResponseTime.set(0);
            lastResetTime = System.currentTimeMillis();
        }
    }
    
    /**
     * 注册缓存实例进行监控
     * 
     * @param name 缓存名称
     * @param cache 缓存操作实例
     */
    public void registerCache(String name, CacheOperations cache) {
        if (!config.isEnabled()) {
            return;
        }
        
        cacheInstances.put(name, cache);
        metricsCollectors.put(name, new PerformanceMetrics());
        
        log.info("[VRML-Cache] Registered cache for monitoring: {}", name);
    }
    
    /**
     * 取消注册缓存实例
     * 
     * @param name 缓存名称
     */
    public void unregisterCache(String name) {
        cacheInstances.remove(name);
        metricsCollectors.remove(name);
        
        log.info("[VRML-Cache] Unregistered cache from monitoring: {}", name);
    }
    
    /**
     * 记录缓存操作指标
     * 
     * @param cacheName 缓存名称
     * @param operation 操作名称
     * @param responseTime 响应时间（毫秒）
     * @param success 是否成功
     */
    public void recordOperation(String cacheName, String operation, long responseTime, boolean success) {
        if (!config.isEnabled() || !config.isEnablePerformanceMetrics()) {
            return;
        }
        
        PerformanceMetrics metrics = metricsCollectors.get(cacheName);
        if (metrics != null) {
            metrics.recordRequest(responseTime, success);
        }
        
        // 记录到VRML指标系统
        String metricName = "vrml.cache." + cacheName + "." + operation;
        VrmlMetricIntegration.recordTime(metricName, Duration.ofMillis(responseTime));
        VrmlMetricIntegration.recordCount(metricName + ".count", success);
        
        if (!success) {
            VrmlMetricIntegration.recordCount(metricName + ".error", true);
        }
    }
    
    /**
     * 获取缓存性能报告
     * 
     * @param cacheName 缓存名称
     * @return 性能报告
     */
    public CachePerformanceReport getPerformanceReport(String cacheName) {
        CacheOperations cache = cacheInstances.get(cacheName);
        PerformanceMetrics metrics = metricsCollectors.get(cacheName);
        
        if (cache == null || metrics == null) {
            return null;
        }
        
        Try<CacheStats> statsResult = cache.getStats();
        CacheStats stats = statsResult.getOrElse(CacheStats.builder().build());
        
        return CachePerformanceReport.builder()
            .cacheName(cacheName)
            .hitRate(stats.getHitRate())
            .missRate(stats.getMissRate())
            .errorRate(metrics.getErrorRate())
            .averageResponseTime(metrics.getAverageResponseTime())
            .maxResponseTime(metrics.getMaxResponseTime())
            .requestCount(metrics.getRequestCount())
            .cacheSize(stats.getSize())
            .evictionCount(stats.getEvictionCount())
            .build();
    }
    
    /**
     * 启动监控
     */
    private void startMonitoring() {
        if (!config.isEnabled()) {
            return;
        }
        
        // 启动性能监控任务
        if (config.isEnablePerformanceMetrics()) {
            scheduler.scheduleAtFixedRate(this::collectMetrics, 
                config.getMonitorInterval().getSeconds(), 
                config.getMonitorInterval().getSeconds(), 
                TimeUnit.SECONDS);
        }
        
        // 启动健康检查任务
        if (config.isEnableHealthCheck()) {
            scheduler.scheduleAtFixedRate(this::performHealthCheck, 
                config.getMonitorInterval().getSeconds(), 
                config.getMonitorInterval().getSeconds(), 
                TimeUnit.SECONDS);
        }
        
        // 启动告警检查任务
        if (config.isEnableAlerts()) {
            scheduler.scheduleAtFixedRate(this::checkAlerts, 
                config.getAlertInterval().getSeconds(), 
                config.getAlertInterval().getSeconds(), 
                TimeUnit.SECONDS);
        }
        
        log.info("[VRML-Cache] Cache monitoring started");
    }
    
    /**
     * 收集性能指标
     */
    private void collectMetrics() {
        Try.run(() -> {
            for (String cacheName : cacheInstances.keySet()) {
                CachePerformanceReport report = getPerformanceReport(cacheName);
                if (report != null) {
                    // 发送指标到监控系统
                    String prefix = "vrml.cache." + cacheName;
                    
                    VrmlMetricIntegration.recordGauge(prefix + ".hit_rate", report.getHitRate());
                    VrmlMetricIntegration.recordGauge(prefix + ".miss_rate", report.getMissRate());
                    VrmlMetricIntegration.recordGauge(prefix + ".error_rate", report.getErrorRate());
                    VrmlMetricIntegration.recordGauge(prefix + ".avg_response_time", report.getAverageResponseTime());
                    VrmlMetricIntegration.recordGauge(prefix + ".max_response_time", report.getMaxResponseTime());
                    VrmlMetricIntegration.recordGauge(prefix + ".size", report.getCacheSize());
                    VrmlMetricIntegration.recordGauge(prefix + ".eviction_count", report.getEvictionCount());
                    
                    log.debug("[VRML-Cache] Collected metrics for cache: {}, hit_rate: {:.2f}, error_rate: {:.2f}", 
                        cacheName, report.getHitRate(), report.getErrorRate());
                }
            }
        }).recover(throwable -> {
            log.warn("[VRML-Cache] Failed to collect metrics", throwable);
            return null;
        });
    }
    
    /**
     * 执行健康检查
     */
    private void performHealthCheck() {
        Try.run(() -> {
            for (Map.Entry<String, CacheOperations> entry : cacheInstances.entrySet()) {
                String cacheName = entry.getKey();
                CacheOperations cache = entry.getValue();
                
                Try<Boolean> healthResult = cache.healthCheck();
                boolean healthy = healthResult.isSuccess() && healthResult.get();
                
                // 记录健康状态指标
                VrmlMetricIntegration.recordGauge("vrml.cache." + cacheName + ".healthy", healthy ? 1.0 : 0.0);
                
                if (!healthy) {
                    log.warn("[VRML-Cache] Health check failed for cache: {}", cacheName);
                    
                    if (config.isEnableAlerts()) {
                        VrmlAlertIntegration.alert("cache.health.failed", 
                            "Cache health check failed: " + cacheName, 
                            healthResult.isFailure() ? healthResult.getCause() : null);
                    }
                } else {
                    log.debug("[VRML-Cache] Health check passed for cache: {}", cacheName);
                }
            }
        }).recover(throwable -> {
            log.warn("[VRML-Cache] Failed to perform health check", throwable);
            return null;
        });
    }
    
    /**
     * 检查告警条件
     */
    private void checkAlerts() {
        Try.run(() -> {
            for (String cacheName : cacheInstances.keySet()) {
                CachePerformanceReport report = getPerformanceReport(cacheName);
                if (report == null) {
                    continue;
                }
                
                // 检查命中率告警
                if (report.getHitRate() < config.getHitRateThreshold()) {
                    VrmlAlertIntegration.alert("cache.hit_rate.low", 
                        String.format("Cache hit rate is low: %s, current: %.2f, threshold: %.2f", 
                            cacheName, report.getHitRate(), config.getHitRateThreshold()), 
                        null);
                }
                
                // 检查错误率告警
                if (report.getErrorRate() > config.getErrorRateThreshold()) {
                    VrmlAlertIntegration.alert("cache.error_rate.high", 
                        String.format("Cache error rate is high: %s, current: %.2f, threshold: %.2f", 
                            cacheName, report.getErrorRate(), config.getErrorRateThreshold()), 
                        null);
                }
                
                // 检查响应时间告警
                if (report.getAverageResponseTime() > config.getResponseTimeThreshold()) {
                    VrmlAlertIntegration.alert("cache.response_time.high", 
                        String.format("Cache response time is high: %s, current: %.2f ms, threshold: %d ms", 
                            cacheName, report.getAverageResponseTime(), config.getResponseTimeThreshold()), 
                        null);
                }
            }
        }).recover(throwable -> {
            log.warn("[VRML-Cache] Failed to check alerts", throwable);
            return null;
        });
    }
    
    /**
     * 重置性能指标
     * 
     * @param cacheName 缓存名称
     */
    public void resetMetrics(String cacheName) {
        PerformanceMetrics metrics = metricsCollectors.get(cacheName);
        if (metrics != null) {
            metrics.reset();
            log.info("[VRML-Cache] Reset metrics for cache: {}", cacheName);
        }
    }
    
    /**
     * 关闭监控
     */
    public void shutdown() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        log.info("[VRML-Cache] Cache monitoring shutdown");
    }
    
    /**
     * 缓存性能报告
     */
    public static class CachePerformanceReport {
        private final String cacheName;
        private final double hitRate;
        private final double missRate;
        private final double errorRate;
        private final double averageResponseTime;
        private final long maxResponseTime;
        private final long requestCount;
        private final long cacheSize;
        private final long evictionCount;
        
        private CachePerformanceReport(Builder builder) {
            this.cacheName = builder.cacheName;
            this.hitRate = builder.hitRate;
            this.missRate = builder.missRate;
            this.errorRate = builder.errorRate;
            this.averageResponseTime = builder.averageResponseTime;
            this.maxResponseTime = builder.maxResponseTime;
            this.requestCount = builder.requestCount;
            this.cacheSize = builder.cacheSize;
            this.evictionCount = builder.evictionCount;
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        // Getters
        public String getCacheName() { return cacheName; }
        public double getHitRate() { return hitRate; }
        public double getMissRate() { return missRate; }
        public double getErrorRate() { return errorRate; }
        public double getAverageResponseTime() { return averageResponseTime; }
        public long getMaxResponseTime() { return maxResponseTime; }
        public long getRequestCount() { return requestCount; }
        public long getCacheSize() { return cacheSize; }
        public long getEvictionCount() { return evictionCount; }
        
        public static class Builder {
            private String cacheName;
            private double hitRate;
            private double missRate;
            private double errorRate;
            private double averageResponseTime;
            private long maxResponseTime;
            private long requestCount;
            private long cacheSize;
            private long evictionCount;
            
            public Builder cacheName(String cacheName) { this.cacheName = cacheName; return this; }
            public Builder hitRate(double hitRate) { this.hitRate = hitRate; return this; }
            public Builder missRate(double missRate) { this.missRate = missRate; return this; }
            public Builder errorRate(double errorRate) { this.errorRate = errorRate; return this; }
            public Builder averageResponseTime(double averageResponseTime) { this.averageResponseTime = averageResponseTime; return this; }
            public Builder maxResponseTime(long maxResponseTime) { this.maxResponseTime = maxResponseTime; return this; }
            public Builder requestCount(long requestCount) { this.requestCount = requestCount; return this; }
            public Builder cacheSize(long cacheSize) { this.cacheSize = cacheSize; return this; }
            public Builder evictionCount(long evictionCount) { this.evictionCount = evictionCount; return this; }
            
            public CachePerformanceReport build() {
                return new CachePerformanceReport(this);
            }
        }
    }
}