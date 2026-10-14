package com.pedidos360.ventas.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pedidos360.ventas.model.dto.VentaEventDTO;
import com.pedidos360.ventas.model.entities.Venta;
import com.pedidos360.ventas.model.request.VentaRequest;
import com.pedidos360.ventas.model.request.VentaRequestActualizar;
import com.pedidos360.ventas.repositories.VentaRepository;

@Service
public class VentaService {

    private final VentaRepository ventaRepo;
    private final VentaProducer ventaProducer;

    // Inyección por constructor
    public VentaService(VentaRepository ventaRepo, VentaProducer ventaProducer) {
        this.ventaRepo = ventaRepo;
        this.ventaProducer = ventaProducer;
    }

    public List<Venta> obtenerVentas(){
        return ventaRepo.findAll();
    }

    public Venta agregarVenta(VentaRequest ventaNueva){
        Venta ventaGuardada = new Venta();
        ventaGuardada.setNombreCliente(ventaNueva.getNombreCliente());
        ventaGuardada.setNombreVendedor(ventaNueva.getNombreVendedor());
        ventaGuardada.setFechaVenta(ventaNueva.getFechaVenta());
        ventaGuardada.setTotalVenta(ventaNueva.getTotalVenta());
        ventaGuardada.setMetodoPago(ventaNueva.getMetodoPago());
        ventaGuardada.setProducto_id_producto(ventaNueva.getProducto_id_producto());
        Venta resultado = ventaRepo.save(ventaGuardada);

        // Notificar a RabbitMQ
        VentaEventDTO evento = VentaEventDTO.builder()
                .idVenta(resultado.getId_venta())
                .nombreCliente(resultado.getNombreCliente())
                .nombreVendedor(resultado.getNombreVendedor())
                .fechaVenta(resultado.getFechaVenta())
                .totalVenta(resultado.getTotalVenta())
                .metodoPago(resultado.getMetodoPago())
                .productoIdProducto(resultado.getProducto_id_producto())
                .tipoEvento("VENTA_REGISTRADA")
                .mensaje("Nueva venta registrada exitosamente")
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
