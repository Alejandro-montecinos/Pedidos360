package com.duocuc.pedidosBackend.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoEstadoEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idPedido;
    private int idVenta;
    private String estado;
    private String timestamp;
    private String comentario;
}

