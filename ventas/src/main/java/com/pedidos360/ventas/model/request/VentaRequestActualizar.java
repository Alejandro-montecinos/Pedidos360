package com.pedidos360.ventas.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
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
    
}
