package com.pedidos360.ventas.model.request;

public class VentaRequest {

    private Integer id_venta;
    private String nombreCliente;
    private String nombreVendedor;
    private String fechaVenta;
    private double totalVenta;
    private String metodoPago;
    private int producto_id_producto;

    public VentaRequest() {
    }

    public VentaRequest(Integer id_venta, String nombreCliente, String nombreVendedor, String fechaVenta, double totalVenta, String metodoPago, int producto_id_producto) {
        this.id_venta = id_venta;
        this.nombreCliente = nombreCliente;
        this.nombreVendedor = nombreVendedor;
        this.fechaVenta = fechaVenta;
        this.totalVenta = totalVenta;
        this.metodoPago = metodoPago;
        this.producto_id_producto = producto_id_producto;
    }

    public Integer getId_venta() {
        return id_venta;
    }

    public void setId_venta(Integer id_venta) {
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
