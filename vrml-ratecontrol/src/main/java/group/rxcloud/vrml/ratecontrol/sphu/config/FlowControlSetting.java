package group.rxcloud.vrml.ratecontrol.sphu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于Config动态更新Flow配置
 */
@Component
public class FlowControlSetting {

    @Autowired
    private FlowControlRule flowControlRule;

    private FlowControlConfig flowControlConfig;

    /**
     * 动态刷新Flow配置
     */
    public void setFlowControlConfig(FlowControlConfig flowControlConfig) {
        this.flowControlConfig = flowControlConfig;
        // flush config
        this.flowControlRule.initFlowRules(flowControlConfig);
    }

    public FlowControlConfig getFlowControlConfig() {
        return flowControlConfig;
    }

    // -- Config

    public static class FlowControlConfig {

        private boolean enableMetric = true;
        private FlowControlParam flowControlParam;
        private List<FlowControlItem> flowControlItems;

        public boolean isEnableMetric() {
            return enableMetric;
        }

        public void setEnableMetric(boolean enableMetric) {
            this.enableMetric = enableMetric;
        }

        public FlowControlParam getFlowControlParam() {
            return flowControlParam;
        }

        public void setFlowControlParam(FlowControlParam flowControlParam) {
            this.flowControlParam = flowControlParam;
        }

        public List<FlowControlItem> getFlowControlItems() {
            return flowControlItems;
        }

        public void setFlowControlItems(List<FlowControlItem> flowControlItems) {
            this.flowControlItems = flowControlItems;
        }
    }

    public static class FlowControlParam {

        /**
         * 默认 coldFactor 为 3，即请求 QPS 从 threshold / 3 开始，经预热时长逐渐升至设定的 QPS 阈值。
         */
        private Integer coldFactor;

        public Integer getColdFactor() {
            return coldFactor;
        }

        public void setColdFactor(Integer coldFactor) {
            this.coldFactor = coldFactor;
        }
    }

    public static class FlowControlItem {

        /**
         * 控制粒度
         */
        private String key;
        /**
         * 最大阈值
         */
        private int maxCount;
        /**
         * 预热时长
         */
        private int warmupSec;

        /**
         * 调用来源
         */
        private String limitApp = "default";

        /**
         * 自旋策略配置
         */
        private FlowControlMode.SpinWaitConfig spinWaitConfig;
        /**
         * MQ重试策略配置
         */
        private FlowControlMode.MqRetryConfig mqRetryConfig;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public void setMaxCount(int maxCount) {
            this.maxCount = maxCount;
        }

        public int getWarmupSec() {
            return warmupSec;
        }

        public void setWarmupSec(int warmupSec) {
            this.warmupSec = warmupSec;
        }

        public String getLimitApp() {
            return limitApp;
        }

        public void setLimitApp(String limitApp) {
            this.limitApp = limitApp;
        }

        public FlowControlMode.SpinWaitConfig getSpinWaitConfig() {
            return spinWaitConfig;
        }

        public void setSpinWaitConfig(FlowControlMode.SpinWaitConfig spinWaitConfig) {
            this.spinWaitConfig = spinWaitConfig;
        }

        public FlowControlMode.MqRetryConfig getMqRetryConfig() {
            return mqRetryConfig;
        }

        public void setMqRetryConfig(FlowControlMode.MqRetryConfig mqRetryConfig) {
            this.mqRetryConfig = mqRetryConfig;
        }
    }
}
