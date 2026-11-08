package com.duocuc.administrativo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duocuc.administrativo.model.AdminApiResponse;
import com.duocuc.administrativo.model.KafkaTopicRequest;
import com.duocuc.administrativo.service.KafkaAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/kafka/admin")
@CrossOrigin(origins = "*")
@Tag(name = "Kafka Admin", description = "API administrativa para gestionar tópicos, particiones, réplicas y consumer groups en Apache Kafka")
public class KafkaAdminController {

    private final KafkaAdminService kafkaAdminService;

    public KafkaAdminController(KafkaAdminService kafkaAdminService) {
        this.kafkaAdminService = kafkaAdminService;
    }

    @PostMapping("/topics")
    @Operation(summary = "Crear o declarar un nuevo tópico Kafka con particiones, réplicas y políticas de retención")
    public ResponseEntity<AdminApiResponse> crearTopico(@Valid @RequestBody KafkaTopicRequest request) {
        AdminApiResponse response = kafkaAdminService.crearTopico(request);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/topics")
    @Operation(summary = "Listar todos los tópicos existentes en el clúster Kafka")
    public ResponseEntity<AdminApiResponse> listarTopicos() {
        AdminApiResponse response = kafkaAdminService.listarTopicos();
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/topics/{nombre}")
    @Operation(summary = "Inspeccionar detalles de un tópico (particiones, réplicas, parámetros de retención)")
    public ResponseEntity<AdminApiResponse> obtenerInfoTopico(@PathVariable String nombre) {
        AdminApiResponse response = kafkaAdminService.obtenerInfoTopico(nombre);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.status(404).body(response);
    }

    @DeleteMapping("/topics/{nombre}")
    @Operation(summary = "Eliminar un tópico del clúster Kafka")
    public ResponseEntity<AdminApiResponse> eliminarTopico(@PathVariable String nombre) {
        AdminApiResponse response = kafkaAdminService.eliminarTopico(nombre);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/consumer-groups")
    @Operation(summary = "Listar e inspeccionar los Consumer Groups activos en el clúster Kafka")
    public ResponseEntity<AdminApiResponse> listarGruposConsumidores() {
        AdminApiResponse response = kafkaAdminService.listarGruposConsumidores();
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
}

