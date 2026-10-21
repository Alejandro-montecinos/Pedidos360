package com.duocuc.pedidosBackend.services;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.duocuc.pedidosBackend.config.RabbitMQConfig;
import com.duocuc.pedidosBackend.model.dto.VentaEventDTO;
import com.duocuc.pedidosBackend.model.entities.Productos360;
import com.duocuc.pedidosBackend.repositories.ProductosRepositories;
import com.rabbitmq.client.Channel;

@Service
public class VentaConsumer {

    private final ProductosRepositories productosRepositories;

    public VentaConsumer(ProductosRepositories productosRepositories) {
        this.productosRepositories = productosRepositories;
    }

    @RabbitListener(queues = RabbitMQConfig.PEDIDOS_QUEUE)
    public void recibirEventoVenta(
            VentaEventDTO evento,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        System.out.println("==================================================");
        System.out.println(" [PedidosBackend Consumer] Mensaje recibido desde RabbitMQ (deliveryTag: " + deliveryTag + "):");
        System.out.println(" - Evento:      " + evento.getTipoEvento());
        System.out.println(" - Venta ID:    " + evento.getIdVenta());
        System.out.println(" - Cliente:     " + evento.getNombreCliente());
        System.out.println(" - Vendedor:    " + evento.getNombreVendedor());
        System.out.println(" - Total:       $" + evento.getTotalVenta());
        System.out.println(" - Metodo Pago: " + evento.getMetodoPago());
        System.out.println(" - Mensaje:     " + evento.getMensaje());
        System.out.println(" - Producto ID: " + evento.getProductoIdProducto());

        try {
            // Regla de validación / simulación para probar DLQ
            if (evento.getMensaje() != null && evento.getMensaje().toUpperCase().contains("ERROR_DLQ")) {
                throw new IllegalArgumentException("Simulacion de fallo: Mensaje marcado con 'ERROR_DLQ'. Desviando a Dead Letter Queue.");
            }

            if (evento.getProductoIdProducto() > 0) {
                productosRepositories.findById(evento.getProductoIdProducto()).ifPresentOrElse(
                    producto -> {
                        System.out.println(" -> [VALIDADO] Producto registrado en catalogo: " 
                                + producto.getNombre() + " (Precio ref: $" + producto.getPrecio() + ")");
                    },
                    () -> {
                        System.out.println(" -> [INFO] Producto ID " 
                                + evento.getProductoIdProducto() + " procesado.");
                    }
                );
            }

            // Confirmación manual (ACK)
            channel.basicAck(deliveryTag, false);
            System.out.println(" -> [ACK] Mensaje con deliveryTag " + deliveryTag + " confirmado exitosamente.");

        } catch (Exception e) {
            System.err.println(" -> [ERROR] Error al procesar mensaje " + deliveryTag + ": " + e.getMessage());
            // Rechazo manual (NACK). requeue=false desvía automáticamente a la DLQ gracias al x-dead-letter-exchange
            channel.basicNack(deliveryTag, false, false);
            System.out.println(" -> [NACK -> DLQ] Mensaje " + deliveryTag + " rechazado y redirigido a 'pedidos.dlq'.");
        } finally {
            System.out.println("==================================================");
        }
    }
}
