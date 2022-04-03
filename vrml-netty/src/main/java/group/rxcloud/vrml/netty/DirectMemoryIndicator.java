package group.rxcloud.vrml.netty;

import group.rxcloud.vrml.log.Logs;
import io.netty.util.internal.PlatformDependent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Out-of-heap memory indicator
 */
@Component
public class DirectMemoryIndicator {

    private static final String BUSINESS_KEY = "netty_direct_memory";
    private static final int _1K = 1024;

    private AtomicLong directMemory;

    private Logs logs;

    @PostConstruct
    public void init() {
        logs = Logs.Factory.getLogs(DirectMemoryIndicator.class)
                .key(BUSINESS_KEY);

        Field directMemoryCounter = ReflectionUtils.findField(PlatformDependent.class, "DIRECT_MEMORY_COUNTER");
        if (directMemoryCounter == null) {
            if (logs.isInfoEnabled()) {
                logs.info("[Vrml.Netty.DirectMemoryIndicator] netty.PlatformDependent cannot found DIRECT_MEMORY_COUNTER, please check netty version.");
            }
            return;
        }
        directMemoryCounter.setAccessible(true);

        try {
            directMemory = ((AtomicLong) directMemoryCounter.get(PlatformDependent.class));
        } catch (IllegalAccessException e) {
            if (logs.isWarnEnabled()) {
                logs.warn("[Vrml.Netty.DirectMemoryIndicator] netty.PlatformDependent cannot get DIRECT_MEMORY_COUNTER, please check netty version.", e);
            }
        }

        if (directMemory != null) {
            Executors.newScheduledThreadPool(1)
                    .scheduleAtFixedRate(this::doReport,
                            0, 5, TimeUnit.SECONDS);
        }
    }

    private void doReport() {
        // use logs.info scope
        if (logs.isInfoEnabled()) {
            try {
                int memoryInkb = (int) PlatformDependent.usedDirectMemory() / _1K;
                logs.info("[Vrml.Netty.DirectMemoryIndicator] netty_direct_memory: {} KB", memoryInkb);
            } catch (Exception e) {
                logs.warn("[Vrml.Netty.DirectMemoryIndicator] netty_direct_memory report error", e);
            }
        }
    }
}
