package com.lzh.config;


import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
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
    private static final String authLogin = "AuthFeign#login(java.lang.String, java.lang.String)";
    private static final String seckillEvictKillCache = "SeckillFeign#evictKillCache()";

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

        // AuthFeign调用限流规则
        FlowRule authLoginRule = new FlowRule(authLogin);
        authLoginRule.setCount(20); // QPS阈值
        authLoginRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rules.add(authLoginRule);

        // SeckillFeign调用限流规则
        FlowRule seckillEvictCacheRule = new FlowRule(seckillEvictKillCache);
        seckillEvictCacheRule.setCount(10); // QPS阈值
        seckillEvictCacheRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rules.add(seckillEvictCacheRule);

        FlowRuleManager.loadRules(rules);
    }

    /**
     * 初始化熔断降级规则
     */
    private void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();

        // AuthFeign熔断降级规则（异常比率）
        DegradeRule authLoginDegradeRule = new DegradeRule(authLogin);
        authLoginDegradeRule.setCount(0.5); // 异常比率阈值
        authLoginDegradeRule.setTimeWindow(10); // 熔断时长（秒）
        authLoginDegradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        rules.add(authLoginDegradeRule);

        // SeckillFeign熔断降级规则（异常比率）
        DegradeRule seckillEvictCacheDegradeRule = new DegradeRule(seckillEvictKillCache);
        seckillEvictCacheDegradeRule.setCount(0.5); // 异常比率阈值
        seckillEvictCacheDegradeRule.setTimeWindow(10); // 熔断时长（秒）
        seckillEvictCacheDegradeRule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);
        rules.add(seckillEvictCacheDegradeRule);

        DegradeRuleManager.loadRules(rules);
    }
}