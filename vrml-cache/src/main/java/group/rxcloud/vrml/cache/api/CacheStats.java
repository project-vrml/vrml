package group.rxcloud.vrml.cache.api;

import lombok.Builder;
import lombok.Data;

/**
 * 缓存统计信息
 * 
 * @author VRML Team
 * @since 1.2.0
 */
@Data
@Builder
public class CacheStats {
    
    /**
     * 缓存命中次数
     */
    private final long hitCount;
    
    /**
     * 缓存未命中次数
     */
    private final long missCount;
    
    /**
     * 缓存加载次数
     */
    private final long loadCount;
    
    /**
     * 缓存加载异常次数
     */
    private final long loadExceptionCount;
    
    /**
     * 缓存总加载时间（纳秒）
     */
    private final long totalLoadTime;
    
    /**
     * 缓存驱逐次数
     */
    private final long evictionCount;
    
    /**
     * 当前缓存大小
     */
    private final long size;
    
    /**
     * 获取缓存命中率
     * 
     * @return 命中率（0.0-1.0）
     */
    public double getHitRate() {
        long totalRequests = hitCount + missCount;
        return totalRequests == 0 ? 0.0 : (double) hitCount / totalRequests;
    }
    
    /**
     * 获取缓存未命中率
     * 
     * @return 未命中率（0.0-1.0）
     */
    public double getMissRate() {
        return 1.0 - getHitRate();
    }
    
    /**
     * 获取平均加载时间（纳秒）
     * 
     * @return 平均加载时间
     */
    public double getAverageLoadTime() {
        return loadCount == 0 ? 0.0 : (double) totalLoadTime / loadCount;
    }
    
    /**
     * 获取加载异常率
     * 
     * @return 加载异常率（0.0-1.0）
     */
    public double getLoadExceptionRate() {
        long totalLoads = loadCount + loadExceptionCount;
        return totalLoads == 0 ? 0.0 : (double) loadExceptionCount / totalLoads;
    }
}