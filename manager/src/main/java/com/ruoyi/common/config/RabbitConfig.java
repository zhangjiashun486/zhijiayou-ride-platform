package com.ruoyi.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbit配置
 */
@Configuration
public class RabbitConfig {
    private static Logger log = LoggerFactory.getLogger(RabbitConfig.class);
    @Autowired
    private CachingConnectionFactory connectionFactory;

    public static final String HITCH_AI_MSG = "HITCH_AI_MSG";
    public static final String HITCH_AI_FILE = "HITCH_AI_FILE";


    /**
     * exchange
     */
//    @Bean
//    DirectExchange directExchange() {
//        return new DirectExchange(CAMPUSAI_EXCHANGE);
//    }
    @Bean
    public Queue getAiMsgQueue() {
        return new Queue(HITCH_AI_MSG);
    }
//    @Bean
//    Binding bindingNoticeDirect() {
//        return BindingBuilder.bind(getNoticeQueue()).to(directExchange()).with(HITCH_AI_MSG);
//    }

    @Bean
    public Queue getAiFileQueue() {
        return new Queue(HITCH_AI_FILE);
    }
//    @Bean
//    Binding bindingDocumentDirect() {
//        return BindingBuilder.bind(getNoticeQueue()).to(directExchange()).with(CAMPUSAI_DOCUMENT);
//    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        //若使用confirm-callback或return-callback，必须要配置publisherConfirms或publisherReturns为true
        //每个rabbitTemplate只能有一个confirm-callback和return-callback，如果这里配置了，那么写生产者的时候不能再写confirm-callback和return-callback
        //使用return-callback时必须设置mandatory为true，或者在配置中设置mandatory-expression的值为true
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
       /**
        * 如果消息没有到exchange,则confirm回调,ack=false
        * 如果消息到达exchange,则confirm回调,ack=true
        * exchange到queue成功,则不回调return
        * exchange到queue失败,则回调return(需设置mandatory=true,否则不回回调,消息就丢了)
        */
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (ack) {
                    log.info("消息发送成功:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
                } else {
                    log.info("消息发送失败:correlationData({}),ack({}),cause({})", correlationData, ack, cause);
                }
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                log.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", exchange, routingKey, replyCode, replyText, message);
            }
        });
        return rabbitTemplate;
    }
}
