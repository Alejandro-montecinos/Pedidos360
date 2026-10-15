package com.pedidos360.ventas.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private int id_producto;
    private String nombre;
    private String descripcion;
    private float precio;
}
