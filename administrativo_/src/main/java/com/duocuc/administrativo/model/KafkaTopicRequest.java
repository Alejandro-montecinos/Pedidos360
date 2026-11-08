package com.duocuc.administrativo.model;

import java.util.Map;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaTopicRequest {

    @NotBlank(message = "El nombre del tópico es obligatorio")
    private String nombre;

    @Min(value = 1, message = "Debe tener al menos 1 partición")
    @Builder.Default
    private int particiones = 3;

    @Min(value = 1, message = "El factor de replicación debe ser al menos 1")
    @Builder.Default
    private short replicacion = 1;

    // Propiedades avanzadas de administración de tópicos requeridas por la rúbrica:
    // retention.ms (ej. "604800000" = 7 días), cleanup.policy (delete / compact), etc.
    private Map<String, String> configs;
}

