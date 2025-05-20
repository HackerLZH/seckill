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
        // rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
        //     log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,ack,cause);
        // });
        // rabbitTemplate.setReturnsCallback(returnedMessage -> {
        //     log.warn("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",returnedMessage.getExchange(),returnedMessage.getRoutingKey(),returnedMessage.getReplyCode(),returnedMessage.getReplyText(),returnedMessage.getMessage());
        // });
        return rabbitTemplate;
    }


    // 用于异步秒杀下单的消息队列
    @Bean
    Queue seckillGoodQueue(){
        return new Queue(Constants.MQ_KILL_GOOD_QUEUE,true);
    }

    @Bean
    TopicExchange seckillGoodExchange(){
        return new TopicExchange(Constants.MQ_KILL_GOOD_EXCHANGE,true,false);
    }

    @Bean
    Binding seckillGoodBinding(){
        return BindingBuilder.bind(seckillGoodQueue()).to(seckillGoodExchange()).with(Constants.MQ_KILL_GOOD_ROUTE);
    }


    // 下单成功后进入延迟队列
    @Bean
    Queue seckillGoodOrderQueue(){
        // 关联死信队列
        Map<String, Object> argsMap= new HashMap<>();
        argsMap.put("x-message-ttl", 10000); // 假设订单存在10秒超时，则进入死信队列
        argsMap.put("x-dead-letter-exchange", Constants.MQ_KILL_GOOD_DLX_EXCHANGE);
        argsMap.put("x-dead-letter-routing-key", Constants.MQ_KILL_GOOD_DLX_ROUTE);
        return new Queue(Constants.MQ_KILL_GOOD_ORDER_QUEUE, true, false, false, argsMap);
    }

    @Bean
    TopicExchange seckillGoodOrderExchange(){
        return new TopicExchange(Constants.MQ_KILL_GOOD_ORDER_EXCHANGE,true,false);
    }

    @Bean
    Binding seckillGoodOrderBinding(){
        return BindingBuilder.bind(seckillGoodOrderQueue()).to(seckillGoodOrderExchange()).with(Constants.MQ_KILL_GOOD_ORDER_ROUTE);
    }

    // 死信队列，交换机，路由
    @Bean
    Queue seckillGoodDlxQueue(){
        return new Queue(Constants.MQ_KILL_GOOD_DLX_QUEUE,true);
    }

    @Bean
    TopicExchange seckillGoodDlxExchange(){
        return new TopicExchange(Constants.MQ_KILL_GOOD_DLX_EXCHANGE,true,false);
    }

    @Bean
    Binding seckillGoodDlxBinding(){
        return BindingBuilder.bind(seckillGoodDlxQueue()).to(seckillGoodDlxExchange()).with(Constants.MQ_KILL_GOOD_DLX_ROUTE);
    }


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































































































