package com.pedidos360.ventas.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "ventas")
public class Venta{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_venta;

    @Column(nullable = false)
    private String nombreCliente;

    @Column(nullable = false)
    private String nombreVendedor;

    @Column(nullable = false)
    private String fechaVenta;

    @Column(nullable = false)
    private double totalVenta;

    @Column(nullable = false)
    private String metodoPago;

    @Column(nullable = false)
    private int producto_id_producto;

}