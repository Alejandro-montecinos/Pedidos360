package com.duocuc.pedidosBackend.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data 
public class ActualizarMarca {
    
    @NotBlank 
    private String nombre;
    @NotBlank 
    private  String descripcion;
    @NotBlank 
    private float precio;



}
