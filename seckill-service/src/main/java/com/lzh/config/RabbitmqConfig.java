package com.lzh.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lzh.utils.Constants;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用化 Rabbitmq 配置
 */
@Slf4j
@Configuration
public class RabbitmqConfig {

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Bean
    MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    /**
     * 单一消费者
     * @return
     */
    // @Bean(name = "singleListenerContainer")
    // SimpleRabbitListenerContainerFactory listenerContainer(){
    //     SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    //     factory.setConnectionFactory(connectionFactory);
    //     factory.setMessageConverter(new Jackson2JsonMessageConverter());
    //     factory.setConcurrentConsumers(1);
    //     factory.setMaxConcurrentConsumers(1);
    //     factory.setPrefetchCount(1);
    //     return factory;
    // }

    /**
     * 多个消费者
     * @return
     * @description 已经在yml中配置
     */
    // @Bean(name = "multiListenerContainer")
    // SimpleRabbitListenerContainerFactory multiListenerContainer(){
    //     SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    //     factory.setConnectionFactory(connectionFactory);
    //     factory.setMessageConverter(jsonMessageConverter());
    //     factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
    //     factory.setConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.concurrency",int.class));
    //     factory.setMaxConcurrentConsumers(env.getProperty("spring.rabbitmq.listener.simple.max-concurrency",int.class));
    //     factory.setPrefetchCount(env.getProperty("spring.rabbitmq.listener.simple.prefetch",int.class));
    //     return factory;
    // }

    @Bean
    RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true); // 消费者在消息没有被路由到合适队列情况下会被return监听，而不会自动删除
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
        });
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            log.warn("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",returnedMessage.getExchange(),returnedMessage.getRoutingKey(),returnedMessage.getReplyCode(),returnedMessage.getReplyText(),returnedMessage.getMessage());
        });
        return rabbitTemplate;
    }


    //秒杀下单通知的消息模型
    @Bean
    public Queue seckillOrderQueue(){
        return new Queue(Constants.MQ_KILL_GOOD_QUEUE,true);
    }

    @Bean
    public TopicExchange seckillOrderExchange(){
        return new TopicExchange(Constants.MQ_KILL_GOOD_EXCHANGE,true,false);
    }

    @Bean
    public Binding seckillOrderBinding(){
        return BindingBuilder.bind(seckillOrderQueue()).to(seckillOrderExchange()).with(Constants.MQ_KILL_GOOD_ROUTE);
    }


    //TODO:构建秒杀成功之后-订单超时未支付的死信队列消息模型

    // @Bean
    // public Queue successKillDeadQueue(){
    //     Map<String, Object> argsMap= new HashMap<>();
    //     argsMap.put("x-dead-letter-exchange",env.getProperty("mq.kill.good.success.dead.exchange"));
    //     argsMap.put("x-dead-letter-routing-key",env.getProperty("mq.kill.good.success.dead.routing.key"));
    //     return new Queue(env.getProperty("mq.kill.good.success.dead.queue"),true,false,false,argsMap);
    // }

    // //基本交换机
    // @Bean
    // public TopicExchange successKillDeadProdExchange(){
    //     return new TopicExchange(env.getProperty("mq.kill.item.success.kill.dead.prod.exchange"),true,false);
    // }

    // //创建基本交换机+基本路由 -> 死信队列 的绑定
    // @Bean
    // public Binding successKillDeadProdBinding(){
    //     return BindingBuilder.bind(successKillDeadQueue()).to(successKillDeadProdExchange()).with(env.getProperty("mq.kill.item.success.kill.dead.prod.routing.key"));
    // }

    // //真正的队列
    // @Bean
    // public Queue successKillRealQueue(){
    //     return new Queue(env.getProperty("mq.kill.item.success.kill.dead.real.queue"),true);
    // }

    // //死信交换机
    // @Bean
    // public TopicExchange successKillDeadExchange(){
    //     return new TopicExchange(env.getProperty("mq.kill.item.success.kill.dead.exchange"),true,false);
    // }

    // //死信交换机+死信路由->真正队列 的绑定
    // @Bean
    // public Binding successKillDeadBinding(){
    //     return BindingBuilder.bind(successKillRealQueue()).to(successKillDeadExchange()).with(env.getProperty("mq.kill.item.success.kill.dead.routing.key"));
    // }


    //TODO:RabbitMQ限流

    // @Bean
    // public Queue executeLimitQueue(){
    //     Map<String, Object> argsMap=Maps.newHashMap();
    //     //限制channel中队列同一时刻通过的消息数量
    //     argsMap.put("x-max-length", env.getProperty("spring.rabbitmq.listener.simple.prefetch",Integer.class));
    //     return new Queue(env.getProperty("mq.kill.item.execute.limit.queue.name"),true,false,false,argsMap);
    // }

    // @Bean
    // public TopicExchange executeLimitExchange(){
    //     return new TopicExchange(env.getProperty("mq.kill.item.execute.limit.queue.exchange"),true,false);
    // }

    // @Bean
    // public Binding executeLimitBinding(){
    //     return BindingBuilder.bind(executeLimitQueue()).to(executeLimitExchange()).with(env.getProperty("mq.kill.item.execute.limit.queue.routing.key"));
    // }
}































































































