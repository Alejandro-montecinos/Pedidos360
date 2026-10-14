package com.pedidos360.ventas.model.request;

import lombok.Data;

@Data
public class VentaRequest {

    private int id_venta;
    private String nombreCliente;
    private String nombreVendedor;
    private String fechaVenta;
    private double totalVenta;
    private String metodoPago;
    private int producto_id_producto;


}
