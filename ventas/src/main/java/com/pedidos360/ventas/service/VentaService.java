package com.pedidos360.ventas.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pedidos360.ventas.model.entities.Venta;
import com.pedidos360.ventas.model.request.VentaRequest;
import com.pedidos360.ventas.model.request.VentaRequestActualizar;
import com.pedidos360.ventas.repositories.VentaRepository;

@Service
public class VentaService {

    private VentaRepository ventaRepo;

    // Inyección por constructor para el repositorio
    public VentaService(VentaRepository ventaRepo) {
        this.ventaRepo = ventaRepo;
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
        return ventaRepo.save(ventaGuardada);

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
