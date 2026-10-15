package com.pedidos360.ventas.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pedidos360.ventas.model.dto.ProductoDTO;
import com.pedidos360.ventas.model.dto.VentaEventDTO;
import com.pedidos360.ventas.model.entities.Venta;
import com.pedidos360.ventas.model.request.VentaRequest;
import com.pedidos360.ventas.model.request.VentaRequestActualizar;
import com.pedidos360.ventas.repositories.VentaRepository;

@Service
public class VentaService {

    private final VentaRepository ventaRepo;
    private final VentaProducer ventaProducer;
    private final ProductoClientService productoClientService;

    // Inyección por constructor
    public VentaService(
            VentaRepository ventaRepo,
            VentaProducer ventaProducer,
            ProductoClientService productoClientService) {
        this.ventaRepo = ventaRepo;
        this.ventaProducer = ventaProducer;
        this.productoClientService = productoClientService;
    }

    public List<Venta> obtenerVentas(){
        return ventaRepo.findAll();
    }

    public Venta agregarVenta(VentaRequest ventaNueva){
        // 1. Validar que el producto existe en pedidosBackend
        int idProducto = ventaNueva.getProducto_id_producto();
        ProductoDTO producto = productoClientService.obtenerProductoPorId(idProducto);

        // 2. Si el producto existe, proceder a guardar la venta en la base de datos
        Venta ventaGuardada = new Venta();
        ventaGuardada.setNombreCliente(ventaNueva.getNombreCliente());
        ventaGuardada.setNombreVendedor(ventaNueva.getNombreVendedor());
        ventaGuardada.setFechaVenta(ventaNueva.getFechaVenta());
        ventaGuardada.setTotalVenta(ventaNueva.getTotalVenta());
        ventaGuardada.setMetodoPago(ventaNueva.getMetodoPago());
        ventaGuardada.setProducto_id_producto(idProducto);
        Venta resultado = ventaRepo.save(ventaGuardada);

        // 3. Notificar a pedidosBackend a través de RabbitMQ
        VentaEventDTO evento = VentaEventDTO.builder()
                .idVenta(resultado.getId_venta())
                .nombreCliente(resultado.getNombreCliente())
                .nombreVendedor(resultado.getNombreVendedor())
                .fechaVenta(resultado.getFechaVenta())
                .totalVenta(resultado.getTotalVenta())
                .metodoPago(resultado.getMetodoPago())
                .productoIdProducto(resultado.getProducto_id_producto())
                .tipoEvento("VENTA_REGISTRADA")
                .mensaje("Venta exitosa del producto: " + producto.getNombre())
                .build();
        ventaProducer.enviarVenta(evento);

        return resultado;
    }

    public VentaEventDTO enviarPruebaRabbitMQ(String mensaje) {
        VentaEventDTO evento = VentaEventDTO.builder()
                .idVenta(1001)
                .nombreCliente("Cliente de Prueba")
                .nombreVendedor("Vendedor Demo")
                .fechaVenta("2026-10-14")
                .totalVenta(25990.0)
                .metodoPago("EFECTIVO")
                .productoIdProducto(1)
                .tipoEvento("PRUEBA_RABBITMQ")
                .mensaje(mensaje != null ? mensaje : "Pedido1001")
                .build();
        ventaProducer.enviarVenta(evento);
        return evento;
    }

    public Venta buscarVentaPorId( int idVenta ){

        Venta ventaExiste = ventaRepo.findById(idVenta).orElse(null);
        if (ventaExiste == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Venta no encontrada.");
        }
        return ventaExiste;
    }

    public Venta actualizarVenta( int idVenta ,VentaRequestActualizar ventaEditada ){
        Venta ventaExiste = ventaRepo.findById(idVenta).orElse(null);
        if (ventaExiste == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Venta no encontrada.");
        }

        // Validar que el nuevo producto exista en pedidosBackend
        productoClientService.obtenerProductoPorId(ventaEditada.getProducto_id_producto());

        ventaExiste.setNombreCliente(ventaEditada.getNombreCliente());
        ventaExiste.setNombreVendedor(ventaEditada.getNombreVendedor());
        ventaExiste.setFechaVenta(ventaEditada.getFechaVenta());
        ventaExiste.setTotalVenta(ventaEditada.getTotalVenta());
        ventaExiste.setMetodoPago(ventaEditada.getMetodoPago());
        ventaExiste.setProducto_id_producto(ventaEditada.getProducto_id_producto());
        return ventaRepo.save(ventaExiste);

    }

    public String eliminarVenta(int idVenta){
        Venta ventaExiste = ventaRepo.findById(idVenta).orElse(null);
        if (ventaExiste != null){
            ventaRepo.deleteById(idVenta);
            return "Venta eliminada correctamente.";
        }
        else{
            return "No se encontró la venta con ID: " + idVenta;
        }

    }


}
