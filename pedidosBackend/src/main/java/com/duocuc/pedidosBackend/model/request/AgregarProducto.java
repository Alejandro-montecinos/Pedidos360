package com.duocuc.pedidosBackend.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 
public class AgregarProducto {
    
    @NotBlank 
    private String nombre;
    @NotBlank 
    private  String descripcion;
    @NotBlank 
    private float precio;
}
