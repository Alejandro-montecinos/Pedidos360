package com.duocuc.administrativo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequest {

    @NotBlank(message = "El nombre del exchange es obligatorio")
    @Schema(description = "Nombre del exchange a crear", example = "pedidos.eventos.exchange")
    private String name;

    @Schema(description = "Tipo de exchange: DIRECT, FANOUT, TOPIC o HEADERS", example = "DIRECT")
    private String type = "DIRECT";

    @Schema(description = "Indica si el exchange sobrevive a reinicios del broker", example = "true")
    private boolean durable = true;

    @Schema(description = "Indica si se borra cuando no haya colas vinculadas", example = "false")
    private boolean autoDelete = false;
}
