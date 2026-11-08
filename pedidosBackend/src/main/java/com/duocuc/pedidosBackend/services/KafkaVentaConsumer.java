package com.duocuc.pedidosBackend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.duocuc.pedidosBackend.config.KafkaConfig;
import com.duocuc.pedidosBackend.model.dto.VentaEventDTO;
import com.duocuc.pedidosBackend.repositories.ProductosRepositories;

@Service
public class KafkaVentaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaVentaConsumer.class);

    private final ProductosRepositories productosRepositories;
    private final KafkaPedidoProducer kafkaPedidoProducer;

    public KafkaVentaConsumer(
            ProductosRepositories productosRepositories,
            KafkaPedidoProducer kafkaPedidoProducer) {
        this.productosRepositories = productosRepositories;
        this.kafkaPedidoProducer = kafkaPedidoProducer;
    }

    /**
     * Consumidor Kafka con @KafkaListener en tópico 'ventas.registradas'
     * Separación clara entre infraestructura de mensajería y dominio de negocio.
     */
    @KafkaListener(
            topics = KafkaConfig.TOPIC_VENTAS_REGISTRADAS,
            groupId = "${spring.kafka.consumer.group-id:pedidos-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void procesarEventoVenta(
            @Payload VentaEventDTO evento,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("==================================================");
        log.info("[PedidosBackend KafkaListener] Evento recibido desde 'ventas.registradas':");
        log.info(" - Partición:   {}", partition);
        log.info(" - Offset:      {}", offset);
        log.info(" - Venta ID:    {}", evento.getIdVenta());
        log.info(" - Cliente:     {}", evento.getNombreCliente());
        log.info(" - Total:       ${}", evento.getTotalVenta());
        log.info(" - Producto ID: {}", evento.getProductoIdProducto());
        log.info(" - Mensaje:     {}", evento.getMensaje());

        try {
            // Validación de payload
            if (evento.getIdVenta() <= 0) {
                log.warn("[PedidosBackend KafkaListener] ID de venta inválido ({}). Descartando.", evento.getIdVenta());
                return;
            }

            // Simulación de control de error si viene mensaje con ERROR_KAFKA
            if (evento.getMensaje() != null && evento.getMensaje().toUpperCase().contains("ERROR_KAFKA")) {
                throw new IllegalArgumentException("Simulación de error en Kafka para prueba de reintentos y tolerancia a fallos.");
            }

            // Validación de dominio en catálogo de productos
            if (evento.getProductoIdProducto() > 0) {
                productosRepositories.findById(evento.getProductoIdProducto()).ifPresentOrElse(
                        producto -> log.info(" -> [DOMINIO VALIDADO] Producto '{}' confirmado en stock (Precio: ${})",
                                producto.getNombre(), producto.getPrecio()),
                        () -> log.info(" -> [INFO] Producto ID {} procesado sin stock directo.", evento.getProductoIdProducto())
                );
            }

            // Responder por el segundo tópico 'pedidos.estado-actualizado' para cerrar el ciclo completo de dos tópicos
            kafkaPedidoProducer.notificarCambioEstado(
                    evento.getIdVenta(), // simulado idPedido
                    evento.getIdVenta(),
                    "EN_PREPARACION",
                    "Pedido validado y en preparación tras evento Kafka de venta"
            );

            log.info(" -> [KAFKA OK] Procesamiento de venta #{} completado y evento de estado emitido.", evento.getIdVenta());

        } catch (Exception e) {
            log.error(" -> [KAFKA ERROR] Error procesando evento de venta #{}: {}", evento.getIdVenta(), e.getMessage());
            throw e; // Lanza para que el ErrorHandler aplique reintentos configurados
        } finally {
            log.info("==================================================");
        }
    }
}

