package com.pedidos360.ventas.model.dto;

import java.io.Serializable;

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

    public VentaEventDTO() {
    }

    public VentaEventDTO(int idVenta, String nombreCliente, String nombreVendedor, String fechaVenta, double totalVenta, String metodoPago, int productoIdProducto, String tipoEvento, String mensaje) {
        this.idVenta = idVenta;
        this.nombreCliente = nombreCliente;
        this.nombreVendedor = nombreVendedor;
        this.fechaVenta = fechaVenta;
        this.totalVenta = totalVenta;
        this.metodoPago = metodoPago;
        this.productoIdProducto = productoIdProducto;
        this.tipoEvento = tipoEvento;
        this.mensaje = mensaje;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
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

    public int getProductoIdProducto() {
        return productoIdProducto;
    }

    public void setProductoIdProducto(int productoIdProducto) {
        this.productoIdProducto = productoIdProducto;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int idVenta;
        private String nombreCliente;
        private String nombreVendedor;
        private String fechaVenta;
        private double totalVenta;
        private String metodoPago;
        private int productoIdProducto;
        private String tipoEvento;
        private String mensaje;

        public Builder idVenta(int idVenta) { this.idVenta = idVenta; return this; }
        public Builder nombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; return this; }
        public Builder nombreVendedor(String nombreVendedor) { this.nombreVendedor = nombreVendedor; return this; }
        public Builder fechaVenta(String fechaVenta) { this.fechaVenta = fechaVenta; return this; }
        public Builder totalVenta(double totalVenta) { this.totalVenta = totalVenta; return this; }
        public Builder metodoPago(String metodoPago) { this.metodoPago = metodoPago; return this; }
        public Builder productoIdProducto(int productoIdProducto) { this.productoIdProducto = productoIdProducto; return this; }
        public Builder tipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; return this; }
        public Builder mensaje(String mensaje) { this.mensaje = mensaje; return this; }

        public VentaEventDTO build() {
            return new VentaEventDTO(idVenta, nombreCliente, nombreVendedor, fechaVenta, totalVenta, metodoPago, productoIdProducto, tipoEvento, mensaje);
        }
    }
}
