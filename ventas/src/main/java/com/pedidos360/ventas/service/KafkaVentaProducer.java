package com.pedidos360.ventas.service;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.pedidos360.ventas.config.KafkaConfig;
import com.pedidos360.ventas.model.dto.VentaEventDTO;

@Service
public class KafkaVentaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaVentaProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaVentaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEventoVenta(VentaEventDTO evento) {
        String key = String.valueOf(evento.getIdVenta());
        log.info("[Kafka Producer] Publicando evento en tópico '{}' con clave '{}'", KafkaConfig.TOPIC_VENTAS_REGISTRADAS, key);

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                KafkaConfig.TOPIC_VENTAS_REGISTRADAS,
                key,
                evento
        );

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[Kafka Producer] Mensaje enviado exitosamente a partición {} con offset {}",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("[Kafka Producer] Error al enviar mensaje a Kafka: {}", ex.getMessage(), ex);
            }
        });
    }

    public void publicarEnTopico(String topico, String key, Object payload) {
        log.info("[Kafka Producer] Publicando mensaje en tópico '{}' con clave '{}'", topico, key);
        kafkaTemplate.send(topico, key, payload)
                .whenComplete((res, ex) -> {
                    if (ex == null) {
                        log.info("[Kafka Producer] Envío exitoso a {} [partición: {}, offset: {}]",
                                topico, res.getRecordMetadata().partition(), res.getRecordMetadata().offset());
                    } else {
                        log.error("[Kafka Producer] Fallo al publicar en {}: {}", topico, ex.getMessage());
                    }
                });
    }
}

