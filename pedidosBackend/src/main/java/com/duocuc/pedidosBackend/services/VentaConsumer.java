package com.duocuc.pedidosBackend.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.duocuc.pedidosBackend.config.RabbitMQConfig;
import com.duocuc.pedidosBackend.model.dto.VentaEventDTO;
import com.duocuc.pedidosBackend.model.entities.Productos360;
import com.duocuc.pedidosBackend.repositories.ProductosRepositories;

@Service
public class VentaConsumer {

    private final ProductosRepositories productosRepositories;

    public VentaConsumer(ProductosRepositories productosRepositories) {
        this.productosRepositories = productosRepositories;
    }

    @RabbitListener(queues = RabbitMQConfig.PEDIDOS_QUEUE)
    public void recibirEventoVenta(VentaEventDTO evento) {
        System.out.println("==================================================");
        System.out.println(" [PedidosBackend Consumer] Mensaje recibido desde RabbitMQ:");
        System.out.println(" - Evento:      " + evento.getTipoEvento());
        System.out.println(" - Venta ID:    " + evento.getIdVenta());
        System.out.println(" - Cliente:     " + evento.getNombreCliente());
        System.out.println(" - Vendedor:    " + evento.getNombreVendedor());
        System.out.println(" - Total:       $" + evento.getTotalVenta());
        System.out.println(" - Metodo Pago: " + evento.getMetodoPago());
        System.out.println(" - Mensaje:     " + evento.getMensaje());
        System.out.println(" - Producto ID: " + evento.getProductoIdProducto());

        if (evento.getProductoIdProducto() > 0) {
            try {
                productosRepositories.findById(evento.getProductoIdProducto()).ifPresentOrElse(
                    producto -> {
                        System.out.println(" -> [VALIDADO] Producto registrado en catalogo: " 
                                + producto.getNombre() + " (Precio ref: $" + producto.getPrecio() + ")");
                    },
                    () -> {
                        System.out.println(" -> [INFO] Producto ID " 
                                + evento.getProductoIdProducto() + " procesado.");
                    }
                );
            } catch (Exception e) {
                System.out.println(" -> [INFO] Notificacion procesada sin consulta de BD: " + e.getMessage());
            }
        }
        System.out.println("==================================================");
    }
}
