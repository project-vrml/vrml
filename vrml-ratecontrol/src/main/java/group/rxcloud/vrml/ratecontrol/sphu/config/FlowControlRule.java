package group.rxcloud.vrml.ratecontrol.sphu.config;

import com.alibaba.csp.sentinel.config.SentinelConfig;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import group.rxcloud.vrml.core.serialization.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * FlowRule初始化
 */
@Component
public class FlowControlRule {

    private static final Logger log = LoggerFactory.getLogger(FlowControlRule.class);

    /**
     * 刷新FlowRule配置
     */
    public void initFlowRules(FlowControlSetting.FlowControlConfig flowControlConfig) {
        log.info("[FlowControlRule.flush] config[{}]", Serialization.toJsonSafe(flowControlConfig));

        FlowControlSetting.FlowControlParam flowControlParam = flowControlConfig.getFlowControlParam();
        if (flowControlParam != null) {
            // 设置预热启动阈值
            Integer coldFactor = flowControlParam.getColdFactor();
            if (coldFactor != null && coldFactor > 0) {
                SentinelConfig.setConfig(SentinelConfig.COLD_FACTOR, String.valueOf(coldFactor));
            }
        }

        List<FlowControlSetting.FlowControlItem> flowControlItems = flowControlConfig.getFlowControlItems();
        if (flowControlParam != null) {
            List<FlowRule> rules = new ArrayList<>(flowControlItems.size());

            for (FlowControlSetting.FlowControlItem flowControlItem : flowControlItems) {
                FlowRule rule = new FlowRule();
                // 控制粒度
                rule.setResource(flowControlItem.getKey());
                // 这里设置QPS最大的阈值
                rule.setCount(flowControlItem.getMaxCount());
                // 基于QPS流控规则
                rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
                // 默认不区分调用来源
                rule.setLimitApp(flowControlItem.getLimitApp());
                // 流控效果, 采用warm up冷启动方式
                rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP);
                // 在一定时间内逐渐增加到阈值上限，给冷系统一个预热的时间，避免冷系统被压垮。
                // warmUpPeriodSec 代表期待系统进入稳定状态的时间（即预热时长）。
                // 默认值为10s
                rule.setWarmUpPeriodSec(flowControlItem.getWarmupSec());
                rules.add(rule);
            }

            FlowRuleManager.loadRules(rules);
        }
    }
}
