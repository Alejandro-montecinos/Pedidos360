package com.duocuc.pedidosBackend.services;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.duocuc.pedidosBackend.config.KafkaConfig;
import com.duocuc.pedidosBackend.model.dto.PedidoEstadoEventDTO;

@Service
public class KafkaPedidoProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaPedidoProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaPedidoProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void notificarCambioEstado(int idPedido, int idVenta, String nuevoEstado, String comentario) {
        PedidoEstadoEventDTO evento = PedidoEstadoEventDTO.builder()
                .idPedido(idPedido)
                .idVenta(idVenta)
                .estado(nuevoEstado)
                .timestamp(LocalDateTime.now().toString())
                .comentario(comentario)
                .build();

        String key = String.valueOf(idPedido);
        log.info("[Kafka Producer] Notificando cambio de estado en '{}' [Key: {}, Estado: {}]",
                KafkaConfig.TOPIC_PEDIDOS_ESTADO, key, nuevoEstado);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaConfig.TOPIC_PEDIDOS_ESTADO,
                key,
                evento
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[Kafka Producer] Evento notificado en partición {} con offset {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("[Kafka Producer] Fallo al publicar evento de estado: {}", ex.getMessage(), ex);
            }
        });
    }

    public void publicarEnTopico(String topico, String key, Object payload) {
        log.info("[Kafka Producer] Enviando evento a tópico '{}' [Key: {}]", topico, key);
        kafkaTemplate.send(topico, key, payload)
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("[Kafka Producer] Envío exitoso a {} [partición: {}, offset: {}]",
                                topico, res.getRecordMetadata().partition(), res.getRecordMetadata().offset());
                    } else {
                        log.error("[Kafka Producer] Error al enviar a {}: {}", topico, ex.getMessage());
                    }
                });
    }
}

