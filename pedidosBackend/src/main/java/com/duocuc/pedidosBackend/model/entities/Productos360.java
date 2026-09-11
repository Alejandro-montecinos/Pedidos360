package com.duocuc.pedidosBackend.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Data
@Table(name = "Producto")
public class Productos360 {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id_producto;

    @Column(nullable = false)
    private String nombre;
    @Column(nullable = false)
    private  String descripcion;
    @Column(nullable = false)
    private float precio;

}

