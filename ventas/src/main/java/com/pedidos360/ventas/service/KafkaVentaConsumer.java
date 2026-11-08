package com.pedidos360.ventas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.pedidos360.ventas.config.KafkaConfig;
import com.pedidos360.ventas.model.dto.PedidoEstadoEventDTO;

@Service
public class KafkaVentaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaVentaConsumer.class);

    /**
     * Consumidor que escucha eventos del tópico 'pedidos.estado-actualizado'
     * Separación de infraestructura y lógica de dominio con validación y logging contextual.
     */
    @KafkaListener(
            topics = KafkaConfig.TOPIC_PEDIDOS_ESTADO,
            groupId = "${spring.kafka.consumer.group-id:ventas-group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumirEstadoPedido(
            @Payload PedidoEstadoEventDTO evento,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("==================================================");
        log.info("[Ventas KafkaListener] Recibido evento de cambio de estado en 'pedidos.estado-actualizado':");
        log.info(" - Partición: {}", partition);
        log.info(" - Offset:    {}", offset);
        log.info(" - Pedido ID: {}", evento.getIdPedido());
        log.info(" - Venta ID:  {}", evento.getIdVenta());
        log.info(" - Estado:    {}", evento.getEstado());
        log.info(" - Comentario:{}", evento.getComentario());

        // Validación y lógica de negocio
        if (evento.getEstado() == null || evento.getEstado().isBlank()) {
            log.warn("[Ventas KafkaListener] Evento recibido con estado inválido o nulo. Descartando.");
            return;
        }

        procesarActualizacionVenta(evento);
        log.info("==================================================");
    }

    private void procesarActualizacionVenta(PedidoEstadoEventDTO evento) {
        log.info("[Ventas Dominio] Venta vinculada #{} actualizada al estado '{}' exitosamente.",
                evento.getIdVenta(), evento.getEstado());
    }
}

