package com.duocuc.administrativo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindingRequest {

    @NotBlank(message = "El nombre de la cola es obligatorio")
    @Schema(description = "Nombre de la cola destino", example = "pedidos.notificaciones.queue")
    private String queueName;

    @NotBlank(message = "El nombre del exchange es obligatorio")
    @Schema(description = "Nombre del exchange origen", example = "pedidos.eventos.exchange")
    private String exchangeName;

    @Schema(description = "Routing key para el filtrado de mensajes", example = "pedidos.notificaciones.#")
    private String routingKey = "";
}
