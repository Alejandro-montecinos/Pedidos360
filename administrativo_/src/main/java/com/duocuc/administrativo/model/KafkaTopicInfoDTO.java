package com.duocuc.administrativo.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaTopicInfoDTO {
    private String nombre;
    private int numeroParticiones;
    private short factorReplicacion;
    private List<Integer> particiones;
    private Map<String, String> configuraciones;
}

