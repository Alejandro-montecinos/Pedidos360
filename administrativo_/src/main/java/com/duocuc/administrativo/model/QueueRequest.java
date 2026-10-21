package com.duocuc.administrativo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueueRequest {

    @NotBlank(message = "El nombre de la cola es obligatorio")
    @Schema(description = "Nombre de la cola a crear", example = "pedidos.notificaciones.queue")
    private String name;

    @Schema(description = "Indica si la cola sobrevive a reinicios de RabbitMQ", example = "true")
    private boolean durable = true;

    @Schema(description = "Indica si se elimina cuando ningún consumidor esté conectado", example = "false")
    private boolean autoDelete = false;

    @Schema(description = "Indica si solo puede ser usada por la conexión actual", example = "false")
    private boolean exclusive = false;

    @Schema(description = "Dead Letter Exchange (DLX) opcional para redirigir fallos", example = "pedidos.dlx")
    private String deadLetterExchange;

    @Schema(description = "Routing key opcional para el Dead Letter", example = "pedidos.dlq.rk")
    private String deadLetterRoutingKey;
}
