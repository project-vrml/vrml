package group.rxcloud.vrml.ratecontrol.sphu.monitor;

import group.rxcloud.vrml.ratecontrol.sphu.config.FlowControlSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 基于Event的埋点，统计分析Flow的控制效果
 */
@Component
public class FlowControlEventMonitor {

    private static final String PASS_SUCCESS = "SPHU_FLOW_SUCCESS";
    private static final String PASS_SPIN = "SPHU_FLOW_SPIN";
    private static final String PASS_SPIN_OVER = "SPHU_FLOW_SPINVOER";
    private static final String PASS_RETRY = "SPHU_FLOW_RETRY";
    private static final String PASS_RETRY_OVER = "SPHU_FLOW_RETRYOVER";

    @Autowired
    private FlowControlSetting flowControlSetting;

    private void logEvent(String metric, String key) {
        // do metric
    }

    /**
     * 记录获取令牌通过的数量
     */
    public void tryPassSuccess(String key) {
        if (flowControlSetting.getFlowControlConfig().isEnableMetric()) {
            this.logEvent(PASS_SUCCESS, key);
        }
    }

    /**
     * 记录获取令牌失败自旋的数量
     */
    public void tryPassSpin(String key) {
        if (flowControlSetting.getFlowControlConfig().isEnableMetric()) {
            this.logEvent(PASS_SPIN, key);
        }
    }

    /**
     * 记录自旋达到最大值从而通过的数量
     */
    public void tryPassSpinOver(String key) {
        if (flowControlSetting.getFlowControlConfig().isEnableMetric()) {
            this.logEvent(PASS_SPIN_OVER, key);
        }
    }

    /**
     * 记录获取令牌失败MQ重试的数量
     */
    public void tryPassRetry(String key) {
        if (flowControlSetting.getFlowControlConfig().isEnableMetric()) {
            this.logEvent(PASS_RETRY, key);
        }
    }

    /**
     * 记录MQ重试达到最大值从而通过的数量
     */
    public void tryPassRetryOver(String key) {
        if (flowControlSetting.getFlowControlConfig().isEnableMetric()) {
            this.logEvent(PASS_RETRY_OVER, key);
        }
    }
}
