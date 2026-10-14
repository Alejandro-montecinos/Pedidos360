package com.pedidos360.ventas.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.pedidos360.ventas.config.RabbitMQConfig;
import com.pedidos360.ventas.model.dto.VentaEventDTO;

@Service
public class VentaProducer {

    private final RabbitTemplate rabbitTemplate;

    public VentaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarVenta(VentaEventDTO ventaEvent) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.PEDIDOS_QUEUE, ventaEvent);
        System.out.println("==================================================");
        System.out.println(" [Ventas Producer] Evento JSON enviado a RabbitMQ:");
        System.out.println(" ID Venta: " + ventaEvent.getIdVenta());
        System.out.println(" Cliente: " + ventaEvent.getNombreCliente());
        System.out.println(" Total: $" + ventaEvent.getTotalVenta());
        System.out.println("==================================================");
    }
}
