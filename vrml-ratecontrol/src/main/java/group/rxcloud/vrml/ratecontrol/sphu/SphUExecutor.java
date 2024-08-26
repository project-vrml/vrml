package group.rxcloud.vrml.ratecontrol.sphu;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import group.rxcloud.vrml.ratecontrol.sphu.config.FlowControlMode;
import group.rxcloud.vrml.ratecontrol.sphu.config.FlowControlSetting;
import group.rxcloud.vrml.ratecontrol.sphu.monitor.FlowControlEventMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 支持多种模式的FlowControl控制器
 */
@Component
public class SphUExecutor {

    @Autowired
    private FlowControlSetting flowControlSetting;
    @Autowired
    private FlowControlEventMonitor flowControlMonitor;

    /**
     * 尝试获取令牌，如果获取失败，使用线程自旋方式进行等待。
     * 如果超过最大自旋次数，则直接获取到令牌通过。
     */
    public void tryWithSpin(String key) {
        FlowControlSetting.FlowControlConfig flowControlConfig = flowControlSetting.getFlowControlConfig();
        flowControlConfig.getFlowControlItems().stream()
                .filter(flowControlItem -> key.equalsIgnoreCase(flowControlItem.getKey()))
                .findFirst()
                .map(FlowControlSetting.FlowControlItem::getSpinWaitConfig)
                // if config present, do flow control
                .ifPresent(spinWaitConfig -> this.tryWithSpin(key, spinWaitConfig));
    }

    /**
     * 尝试获取令牌，如果获取失败，使用线程自旋方式进行等待。
     * 如果超过最大自旋次数，则直接获取到令牌通过。
     */
    public void tryWithSpin(String key, FlowControlMode.SpinWaitConfig spinWaitConfig) {
        int sleepMills = spinWaitConfig.getSleepMills();
        int maxSpinCount = spinWaitConfig.getMaxSpinCount();

        Entry entry = null;
        for (int i = 0; i < maxSpinCount; i++) {
            try {
                entry = SphU.entry(key);
                flowControlMonitor.tryPassSuccess(key);
                return;
            } catch (BlockException e) {
                flowControlMonitor.tryPassSpin(key);
                try {
                    TimeUnit.MILLISECONDS.sleep(sleepMills);
                } catch (InterruptedException ex) {
                    flowControlMonitor.tryPassSuccess(key);
                    return;
                }
            } finally {
                if (entry != null) {
                    entry.exit();
                }
            }
        }
        flowControlMonitor.tryPassSpinOver(key);
    }
}
