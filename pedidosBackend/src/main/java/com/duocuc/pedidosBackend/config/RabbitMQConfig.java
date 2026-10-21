package com.duocuc.pedidosBackend.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
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

    // 1. Direct Exchange para Dead Letter (DLX)
    @Bean
    public DirectExchange pedidosDlx() {
        return new DirectExchange(PEDIDOS_DLX);
    }

    // 2. Cola para mensajes muertos / fallidos (DLQ)
    @Bean
    public Queue pedidosDlq() {
        return QueueBuilder.durable(PEDIDOS_DLQ).build();
    }

    // 3. Vinculación (Binding) entre DLX y DLQ
    @Bean
    public Binding dlqBinding(Queue pedidosDlq, DirectExchange pedidosDlx) {
        return BindingBuilder.bind(pedidosDlq).to(pedidosDlx).with(PEDIDOS_DLQ_ROUTING_KEY);
    }

    // 4. Cola Principal configurada con argumentos Dead Letter
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
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setAlwaysConvertToInferredType(true);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
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
            System.out.println(" [PedidosBackend] RabbitMQ conectado exitosamente");
            System.out.println(" [PedidosBackend] Cola principal: " + PEDIDOS_QUEUE);
            System.out.println(" [PedidosBackend] DLX (Dead Letter Exchange): " + PEDIDOS_DLX);
            System.out.println(" [PedidosBackend] DLQ (Dead Letter Queue):    " + PEDIDOS_DLQ);
            System.out.println("==================================================");
        };
    }
}
