package com.pedidos360.ventas.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PEDIDOS_QUEUE = "pedidos.queue";
    public static final String PEDIDOS_DLX = "pedidos.dlx";
    public static final String PEDIDOS_DLQ = "pedidos.dlq";
    public static final String PEDIDOS_DLQ_ROUTING_KEY = "pedidos.dlq.rk";

    @Bean
    public DirectExchange pedidosDlx() {
        return new DirectExchange(PEDIDOS_DLX);
    }

    @Bean
    public Queue pedidosDlq() {
        return QueueBuilder.durable(PEDIDOS_DLQ).build();
    }

    @Bean
    public Binding dlqBinding(Queue pedidosDlq, DirectExchange pedidosDlx) {
        return BindingBuilder.bind(pedidosDlq).to(pedidosDlx).with(PEDIDOS_DLQ_ROUTING_KEY);
    }

    @Bean
    public Queue pedidosQueue() {
        return QueueBuilder.durable(PEDIDOS_QUEUE)
                .deadLetterExchange(PEDIDOS_DLX)
                .deadLetterRoutingKey(PEDIDOS_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public ApplicationRunner inicializarRabbitMQ(
            RabbitAdmin rabbitAdmin,
            DirectExchange pedidosDlx,
            Queue pedidosDlq,
            Binding dlqBinding,
            Queue pedidosQueue) {
        return args -> {
            rabbitAdmin.declareExchange(pedidosDlx);
            rabbitAdmin.declareQueue(pedidosDlq);
            rabbitAdmin.declareBinding(dlqBinding);
            rabbitAdmin.declareQueue(pedidosQueue);
            System.out.println("==================================================");
            System.out.println(" [Ventas] RabbitMQ conectado correctamente");
            System.out.println(" [Ventas] Cola principal: " + PEDIDOS_QUEUE);
            System.out.println(" [Ventas] DLX: " + PEDIDOS_DLX + " -> DLQ: " + PEDIDOS_DLQ);
            System.out.println("==================================================");
        };
    }
}
