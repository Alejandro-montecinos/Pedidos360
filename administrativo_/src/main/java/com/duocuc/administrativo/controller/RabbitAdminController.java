package com.duocuc.administrativo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duocuc.administrativo.model.AdminApiResponse;
import com.duocuc.administrativo.model.BindingRequest;
import com.duocuc.administrativo.model.ExchangeRequest;
import com.duocuc.administrativo.model.QueueRequest;
import com.duocuc.administrativo.service.RabbitAdminService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rabbitmq/admin")
@CrossOrigin(origins = "*")
@Tag(name = "RabbitMQ Admin", description = "API administrativa propia para crear y eliminar colas, exchanges y bindings")
public class RabbitAdminController {

    private final RabbitAdminService rabbitAdminService;

    public RabbitAdminController(RabbitAdminService rabbitAdminService) {
        this.rabbitAdminService = rabbitAdminService;
    }

    // ==========================================
    // 1. COLAS (QUEUES)
    // ==========================================

    @PostMapping("/queues")
    @Operation(summary = "Crear o declarar una cola en RabbitMQ")
    public ResponseEntity<AdminApiResponse> crearCola(@Valid @RequestBody QueueRequest request) {
        AdminApiResponse response = rabbitAdminService.crearCola(request);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/queues/{nombre}")
    @Operation(summary = "Eliminar una cola existente de RabbitMQ")
    public ResponseEntity<AdminApiResponse> eliminarCola(
            @PathVariable String nombre,
            @RequestParam(defaultValue = "false") boolean unused,
            @RequestParam(defaultValue = "false") boolean empty) {
        AdminApiResponse response = rabbitAdminService.eliminarCola(nombre, unused, empty);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/queues/{nombre}")
    @Operation(summary = "Obtener propiedades y estado de una cola (mensajes pendientes, consumidores)")
    public ResponseEntity<AdminApiResponse> obtenerInfoCola(@PathVariable String nombre) {
        AdminApiResponse response = rabbitAdminService.obtenerInfoCola(nombre);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.status(404).body(response);
    }

    @PostMapping("/queues/{nombre}/purge")
    @Operation(summary = "Vaciar / purgar todos los mensajes de una cola")
    public ResponseEntity<AdminApiResponse> purgarCola(@PathVariable String nombre) {
        AdminApiResponse response = rabbitAdminService.purgarCola(nombre);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // ==========================================
    // 2. EXCHANGES
    // ==========================================

    @PostMapping("/exchanges")
    @Operation(summary = "Crear o declarar un Exchange (DIRECT, FANOUT, TOPIC, HEADERS)")
    public ResponseEntity<AdminApiResponse> crearExchange(@Valid @RequestBody ExchangeRequest request) {
        AdminApiResponse response = rabbitAdminService.crearExchange(request);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/exchanges/{nombre}")
    @Operation(summary = "Eliminar un Exchange de RabbitMQ")
    public ResponseEntity<AdminApiResponse> eliminarExchange(@PathVariable String nombre) {
        AdminApiResponse response = rabbitAdminService.eliminarExchange(nombre);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    // ==========================================
    // 3. BINDINGS (VINCULACIONES)
    // ==========================================

    @PostMapping("/bindings")
    @Operation(summary = "Crear vinculación (Binding) entre una cola y un exchange con routing key")
    public ResponseEntity<AdminApiResponse> crearBinding(@Valid @RequestBody BindingRequest request) {
        AdminApiResponse response = rabbitAdminService.crearBinding(request);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/bindings")
    @Operation(summary = "Eliminar una vinculación (Binding) existente")
    public ResponseEntity<AdminApiResponse> eliminarBinding(@Valid @RequestBody BindingRequest request) {
        AdminApiResponse response = rabbitAdminService.eliminarBinding(request);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
}
