package com.duocuc.administrativo.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaConsumerGroupDTO {
    private String groupId;
    private String state;
    private String protocolType;
    private int miembrosCount;
    private List<String> miembros;
}

