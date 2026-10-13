package com.lzh.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel配置类
 * 用于配置流控规则和熔断降级规则
 */
@Configuration
public class SentinelConfig {
    private static final String checkOrder = "SeckillFeign#checkOrder(java.lang.Long, java.lang.Integer)";
    private static final String postprocess = "SeckillFeign#postprocess(java.lang.Long)";
    private static final String insertQrCode = "SeckillFeign#insertQrCode(java.lang.Long, java.lang.String)";

    /**
     * 初始化Sentinel规则
     */
    @PostConstruct
    public void initFlowRules() {
        initFlowQpsRules();
        initDegradeRules();
    }

    /**
     * 初始化流控规则（QPS限流）
     */
    private void initFlowQpsRules() {
        List<FlowRule> rules = new ArrayList<>();

        // PaymentService调用SeckillService的接口限流规则
        FlowRule checkOrderRule = new FlowRule(checkOrder);
        checkOrderRule.setCount(20); // QPS阈值
        checkOrderRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rules.add(checkOrderRule);

        FlowRule postprocessRule = new FlowRule(postprocess);
        postprocessRule.setCount(20); // QPS阈值
        postprocessRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rules.add(postprocessRule);

        FlowRule insertQrCodeRule = new FlowRule(insertQrCode);
        insertQrCodeRule.setCount(20); // QPS阈值
        insertQrCodeRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rules.add(insertQrCodeRule);

        FlowRuleManager.loadRules(rules);
    }

    /**
     * 初始化熔断降级规则
     */
    private void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();

        // PaymentService调用SeckillService的接口熔断降级规则（异常比率）
        DegradeRule checkOrderDegradeRule = new DegradeRule(checkOrder);
        checkOrderDegradeRule.setCount(0.5); // 异常比率阈值
        checkOrderDegradeRule.setTimeWindow(10); // 熔断时长（秒）
        checkOrderDegradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        rules.add(checkOrderDegradeRule);

        DegradeRule postprocessDegradeRule = new DegradeRule(postprocess);
        postprocessDegradeRule.setCount(0.5); // 异常比率阈值
        postprocessDegradeRule.setTimeWindow(10); // 熔断时长（秒）
        postprocessDegradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        rules.add(postprocessDegradeRule);

        DegradeRule insertQrCodeDegradeRule = new DegradeRule(insertQrCode);
        insertQrCodeDegradeRule.setCount(0.5); // 异常比率阈值
        insertQrCodeDegradeRule.setTimeWindow(10); // 熔断时长（秒）
        insertQrCodeDegradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        rules.add(insertQrCodeDegradeRule);

        DegradeRuleManager.loadRules(rules);
    }
}