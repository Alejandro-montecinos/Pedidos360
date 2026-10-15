package com.duocuc.bff.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearVentaDto {
    private String nombreCliente;
    private String nombreVendedor;
    private String fechaVenta;
    private double totalVenta;
    private String metodoPago;
    private int producto_id_producto;
}
