package com.duocuc.pedidosBackend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duocuc.pedidosBackend.model.entities.Productos360;
import com.duocuc.pedidosBackend.model.request.ActualizarMarca;
import com.duocuc.pedidosBackend.model.request.AgregarProducto;
import com.duocuc.pedidosBackend.services.ProductosService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController 
@RequestMapping("api/v1/productos")
public class ProductoController {

    @Autowired 
    ProductosService productosService;

    @Autowired
    com.duocuc.pedidosBackend.services.KafkaPedidoProducer kafkaPedidoProducer;

    @PostMapping("/pedidos/{idPedido}/estado")
    public String actualizarEstadoPedido(
            @PathVariable int idPedido,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "1") int idVenta,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "EN_DESPACHO") String estado,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "Actualizado manualmente") String comentario) {
        kafkaPedidoProducer.notificarCambioEstado(idPedido, idVenta, estado, comentario);
        return "Evento de estado de pedido publicado en Kafka (tópico pedidos.estado-actualizado)";
    }


    @GetMapping("")
    public List<Productos360> obtenerTodosLosProductos() {
        return productosService.obtenerTodosLosProductos();
    }

    @GetMapping("/{idP}")
    public Productos360 obtenerProductoPorId(@PathVariable  int idP) {
        return productosService.obtenerProductosPorId(idP);
    }

    @PostMapping("")
    public Productos360 agregarProducto (@RequestBody AgregarProducto agregarProducto) {
        Productos360 prod = productosService.agregarProducto(agregarProducto);
        if (prod != null && kafkaPedidoProducer != null) {
            kafkaPedidoProducer.publicarEnTopico(
                com.duocuc.pedidosBackend.config.KafkaConfig.TOPIC_PRODUCTOS_STOCK,
                String.valueOf(prod.getId_producto()),
                java.util.Map.of(
                    "evento", "PRODUCTO_CREADO",
                    "idProducto", prod.getId_producto(),
                    "nombre", prod.getNombre(),
                    "precio", prod.getPrecio(),
                    "timestamp", java.time.LocalDateTime.now().toString()
                )
            );
            kafkaPedidoProducer.publicarEnTopico(
                com.duocuc.pedidosBackend.config.KafkaConfig.TOPIC_AUDITORIA_EVENTOS,
                String.valueOf(prod.getId_producto()),
                java.util.Map.of(
                    "modulo", "INVENTARIO",
                    "accion", "CREACION_PRODUCTO",
                    "detalle", "Producto creado: " + prod.getNombre(),
                    "timestamp", java.time.LocalDateTime.now().toString()
                )
            );
        }
        return prod;
    }


    @PutMapping("/{idP}")
    public Productos360 actualizarProducto (@RequestBody ActualizarMarca actualizarMarca, @PathVariable int idP){
        Productos360 prod = productosService.actualizarProducto(actualizarMarca, idP);
        if (prod != null && kafkaPedidoProducer != null) {
            kafkaPedidoProducer.publicarEnTopico(
                com.duocuc.pedidosBackend.config.KafkaConfig.TOPIC_PRODUCTOS_STOCK,
                String.valueOf(idP),
                java.util.Map.of(
                    "evento", "PRODUCTO_ACTUALIZADO",
                    "idProducto", idP,
                    "nombre", prod.getNombre(),
                    "precio", prod.getPrecio(),
                    "timestamp", java.time.LocalDateTime.now().toString()
                )
            );
            kafkaPedidoProducer.publicarEnTopico(
                com.duocuc.pedidosBackend.config.KafkaConfig.TOPIC_AUDITORIA_EVENTOS,
                String.valueOf(idP),
                java.util.Map.of(
                    "modulo", "INVENTARIO",
                    "accion", "ACTUALIZACION_PRODUCTO",
                    "detalle", "Producto actualizado ID: " + idP,
                    "timestamp", java.time.LocalDateTime.now().toString()
                )
            );
        }
        return prod;
    }

    @DeleteMapping("/{idP}")
    public String eliminarProductoString(@PathVariable int idP){
        return productosService.eliminarProducto(idP); 
    }

    

    
    
    
}
