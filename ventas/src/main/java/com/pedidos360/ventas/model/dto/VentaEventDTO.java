package com.pedidos360.ventas.model.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaEventDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idVenta;
    private String nombreCliente;
    private String nombreVendedor;
    private String fechaVenta;
    private double totalVenta;
    private String metodoPago;
    private int productoIdProducto;
    private String tipoEvento;
    private String mensaje;
}
