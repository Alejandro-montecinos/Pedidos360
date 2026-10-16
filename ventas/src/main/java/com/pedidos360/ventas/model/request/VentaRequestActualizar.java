package com.pedidos360.ventas.model.request;

import jakarta.validation.constraints.NotBlank;

public class VentaRequestActualizar {

    @NotBlank(message = "El ID de la venta no es opcional")
    private int id_venta;

    @NotBlank(message = "El nombre del cliente no es opcional")
    private String nombreCliente;

    @NotBlank(message = "El nombre del vendedor no es opcional")
    private String nombreVendedor;

    @NotBlank(message = "La fecha de la venta no es opcional")
    private String fechaVenta;

    @NotBlank(message = "El total de la venta no es opcional")
    private double totalVenta;

    @NotBlank(message = "El método de pago no es opcional")
    private String metodoPago;

    @NotBlank(message = "El ID del producto no es opcional")
    private int producto_id_producto;

    public VentaRequestActualizar() {
    }

    public VentaRequestActualizar(int id_venta, String nombreCliente, String nombreVendedor, String fechaVenta, double totalVenta, String metodoPago, int producto_id_producto) {
        this.id_venta = id_venta;
        this.nombreCliente = nombreCliente;
        this.nombreVendedor = nombreVendedor;
        this.fechaVenta = fechaVenta;
        this.totalVenta = totalVenta;
        this.metodoPago = metodoPago;
        this.producto_id_producto = producto_id_producto;
    }

    public int getId_venta() {
        return id_venta;
    }

    public void setId_venta(int id_venta) {
        this.id_venta = id_venta;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreVendedor() {
        return nombreVendedor;
    }

    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public double getTotalVenta() {
        return totalVenta;
    }

    public void setTotalVenta(double totalVenta) {
        this.totalVenta = totalVenta;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public int getProducto_id_producto() {
        return producto_id_producto;
    }

    public void setProducto_id_producto(int producto_id_producto) {
        this.producto_id_producto = producto_id_producto;
    }
}
