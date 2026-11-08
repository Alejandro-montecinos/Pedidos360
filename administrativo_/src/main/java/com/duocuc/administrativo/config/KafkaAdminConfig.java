package com.duocuc.administrativo.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaAdminConfig {

    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    public static final String TOPIC_VENTAS_REGISTRADAS = "ventas.registradas";
    public static final String TOPIC_PEDIDOS_ESTADO = "pedidos.estado-actualizado";
    public static final String TOPIC_PRODUCTOS_STOCK = "productos.stock-actualizado";
    public static final String TOPIC_AUDITORIA_EVENTOS = "auditoria.eventos";

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public AdminClient adminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return AdminClient.create(configs);
    }

    // Declaración programática de tópicos requeridos por la rúbrica con retención, cleanup policy, particiones y réplicas
    @Bean
    public NewTopic topicVentasRegistradas() {
        return TopicBuilder.name(TOPIC_VENTAS_REGISTRADAS)
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "604800000") // 7 días de retención
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .build();
    }

    @Bean
    public NewTopic topicPedidosEstado() {
        return TopicBuilder.name(TOPIC_PEDIDOS_ESTADO)
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "604800000")
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .build();
    }

    @Bean
    public NewTopic topicProductosStock() {
        return TopicBuilder.name(TOPIC_PRODUCTOS_STOCK)
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "604800000")
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .build();
    }

    @Bean
    public NewTopic topicAuditoriaEventos() {
        return TopicBuilder.name(TOPIC_AUDITORIA_EVENTOS)
                .partitions(3)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG, "2592000000") // 30 días de retención
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE)
                .build();
    }
}

