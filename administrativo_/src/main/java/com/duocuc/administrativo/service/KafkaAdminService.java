package com.duocuc.administrativo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.stereotype.Service;

import com.duocuc.administrativo.model.AdminApiResponse;
import com.duocuc.administrativo.model.KafkaConsumerGroupDTO;
import com.duocuc.administrativo.model.KafkaTopicInfoDTO;
import com.duocuc.administrativo.model.KafkaTopicRequest;

@Service
public class KafkaAdminService {

    private final AdminClient adminClient;

    public KafkaAdminService(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    /**
     * Crear un nuevo tópico con particiones, factor de replicación y parámetros avanzados (retention, cleanup policy)
     */
    public AdminApiResponse crearTopico(KafkaTopicRequest request) {
        try {
            NewTopic newTopic = new NewTopic(request.getNombre(), request.getParticiones(), request.getReplicacion());
            if (request.getConfigs() != null && !request.getConfigs().isEmpty()) {
                newTopic.configs(request.getConfigs());
            }

            adminClient.createTopics(Collections.singletonList(newTopic)).all().get(10, TimeUnit.SECONDS);

            return AdminApiResponse.builder()
                    .success(true)
                    .message("Tópico '" + request.getNombre() + "' creado exitosamente en Kafka.")
                    .data(Map.of(
                            "nombre", request.getNombre(),
                            "particiones", request.getParticiones(),
                            "replicacion", request.getReplicacion(),
                            "configs", request.getConfigs() != null ? request.getConfigs() : Map.of()
                    ))
                    .build();
        } catch (Exception e) {
            return AdminApiResponse.builder()
                    .success(false)
                    .message("Error al crear tópico Kafka '" + request.getNombre() + "': " + e.getMessage())
                    .build();
        }
    }

    /**
     * Listar todos los tópicos creados en el clúster Kafka
     */
    public AdminApiResponse listarTopicos() {
        try {
            Set<String> topics = adminClient.listTopics().names().get(10, TimeUnit.SECONDS);
            return AdminApiResponse.builder()
                    .success(true)
                    .message("Tópicos de Kafka recuperados exitosamente.")
                    .data(topics)
                    .build();
        } catch (Exception e) {
            return AdminApiResponse.builder()
                    .success(false)
                    .message("Error al listar tópicos: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Obtener detalles de un tópico específico (particiones, réplicas, configuraciones de retención)
     */
    public AdminApiResponse obtenerInfoTopico(String nombreTopico) {
        try {
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Collections.singletonList(nombreTopico));
            TopicDescription topicDesc = describeTopicsResult.allTopicNames().get(10, TimeUnit.SECONDS).get(nombreTopico);

            List<Integer> particionesIds = topicDesc.partitions().stream()
                    .map(p -> p.partition())
                    .collect(Collectors.toList());

            short replicas = topicDesc.partitions().isEmpty() ? 1 : (short) topicDesc.partitions().get(0).replicas().size();

            // Consultar configuraciones del tópico (ej. retention.ms, cleanup.policy)
            Map<String, String> configsMap = new HashMap<>();
            try {
                ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, nombreTopico);
                DescribeConfigsResult configsResult = adminClient.describeConfigs(Collections.singletonList(configResource));
                org.apache.kafka.clients.admin.Config config = configsResult.all().get(10, TimeUnit.SECONDS).get(configResource);
                config.entries().forEach(entry -> {
                    if (entry.name().contains("retention") || entry.name().contains("cleanup") || entry.name().contains("segment")) {
                        configsMap.put(entry.name(), entry.value());
                    }
                });
            } catch (Exception ex) {
                // Configs opcionales
            }

            KafkaTopicInfoDTO dto = KafkaTopicInfoDTO.builder()
                    .nombre(nombreTopico)
                    .numeroParticiones(topicDesc.partitions().size())
                    .factorReplicacion(replicas)
                    .particiones(particionesIds)
                    .configuraciones(configsMap)
                    .build();

            return AdminApiResponse.builder()
                    .success(true)
                    .message("Información del tópico obtenida exitosamente.")
                    .data(dto)
                    .build();
        } catch (Exception e) {
            return AdminApiResponse.builder()
                    .success(false)
                    .message("No fue posible obtener información del tópico '" + nombreTopico + "': " + e.getMessage())
                    .build();
        }
    }

    /**
     * Eliminar un tópico existente
     */
    public AdminApiResponse eliminarTopico(String nombreTopico) {
        try {
            adminClient.deleteTopics(Collections.singletonList(nombreTopico)).all().get(10, TimeUnit.SECONDS);
            return AdminApiResponse.builder()
                    .success(true)
                    .message("Tópico '" + nombreTopico + "' eliminado exitosamente.")
                    .build();
        } catch (Exception e) {
            return AdminApiResponse.builder()
                    .success(false)
                    .message("Error al eliminar tópico: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Inspeccionar Grupos de Consumidores (Consumer Groups) para balanceo y resiliencia
     */
    public AdminApiResponse listarGruposConsumidores() {
        try {
            List<ConsumerGroupListing> listings = new ArrayList<>(adminClient.listConsumerGroups().all().get(10, TimeUnit.SECONDS));
            List<String> groupIds = listings.stream().map(ConsumerGroupListing::groupId).collect(Collectors.toList());

            Map<String, ConsumerGroupDescription> descriptions = adminClient.describeConsumerGroups(groupIds).all().get(10, TimeUnit.SECONDS);

            List<KafkaConsumerGroupDTO> resultados = new ArrayList<>();
            for (String gId : groupIds) {
                ConsumerGroupDescription desc = descriptions.get(gId);
                List<String> miembros = desc.members().stream()
                        .map(m -> m.consumerId() + " [" + m.host() + "]")
                        .collect(Collectors.toList());

                resultados.add(KafkaConsumerGroupDTO.builder()
                        .groupId(gId)
                        .state(desc.state() != null ? desc.state().toString() : "UNKNOWN")
                        .protocolType(desc.type() != null ? desc.type().toString() : "consumer")
                        .miembrosCount(desc.members().size())
                        .miembros(miembros)
                        .build());
            }

            return AdminApiResponse.builder()
                    .success(true)
                    .message("Grupos de consumidores inspeccionados exitosamente.")
                    .data(resultados)
                    .build();
        } catch (Exception e) {
            return AdminApiResponse.builder()
                    .success(false)
                    .message("Error al inspeccionar grupos de consumidores: " + e.getMessage())
                    .build();
        }
    }
}
