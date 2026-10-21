package com.duocuc.pedidosBackend.services;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.duocuc.pedidosBackend.config.RabbitMQConfig;
import com.duocuc.pedidosBackend.model.dto.VentaEventDTO;
import com.rabbitmq.client.Channel;

/**
 * Consumidor opcional de la cola Dead Letter Queue (pedidos.dlq).
 * 
 * Por defecto está deshabilitado ('rabbitmq.dlq.consumer.enabled=false') para permitir
 * que los mensajes rechazados permanezcan en la cola 'pedidos.dlq' y puedan ser
 * inspeccionados en la consola web de RabbitMQ (http://localhost:15672).
 * 
 * Si se desea activar el procesamiento o drenado automático de la DLQ para auditoría,
 * basta con cambiar 'rabbitmq.dlq.consumer.enabled=true' en application.properties.
 */
@Service
@ConditionalOnProperty(name = "rabbitmq.dlq.consumer.enabled", havingValue = "true", matchIfMissing = false)
public class VentaDlqConsumer {

    @RabbitListener(queues = RabbitMQConfig.PEDIDOS_DLQ)
    public void procesarMensajeDlq(
            VentaEventDTO evento,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(" [PedidosBackend DLQ Consumer] Mensaje recibido en Dead Letter Queue:");
        System.out.println(" - Venta ID:    " + evento.getIdVenta());
        System.out.println(" - Cliente:     " + evento.getNombreCliente());
        System.out.println(" - Total:       $" + evento.getTotalVenta());
        System.out.println(" - Motivo/Msg:  " + evento.getMensaje());
        System.out.println(" -> Almacenado / Registrado para resolucion o reintento.");
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        // Confirmar lectura del mensaje en la DLQ
        channel.basicAck(deliveryTag, false);
    }
}
