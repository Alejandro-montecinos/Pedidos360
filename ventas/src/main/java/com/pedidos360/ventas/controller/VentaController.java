package com.pedidos360.ventas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pedidos360.ventas.model.entities.Venta;
import com.pedidos360.ventas.model.request.VentaRequest;
import com.pedidos360.ventas.model.request.VentaRequestActualizar;
import com.pedidos360.ventas.service.VentaService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/ventas")
@RestController
@CrossOrigin(origins = "*")
public class VentaController {

    private VentaService ventaService;


    // Inyección por constructor
    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("")
    public List<Venta> obtenerVentas() {
        return ventaService.obtenerVentas();
    }

    @GetMapping("/{idVenta}")
    public Venta obtenerVentaPorId(@PathVariable int idVenta) {
        return ventaService.buscarVentaPorId(idVenta);
    }

    @PostMapping("")
    public Venta agregarVenta(@RequestBody VentaRequest agregarVenta){
        return ventaService.agregarVenta(agregarVenta);
    }

    @PutMapping("/{idVenta}")
    public Venta actualizarVenta(@RequestBody VentaRequestActualizar ventaRequestActualizar, @PathVariable int idVenta) {
        return ventaService.actualizarVenta( idVenta, ventaRequestActualizar);
    }

    @DeleteMapping("/{idVenta}")
    public String eliminarVenta(@PathVariable int idVenta) {
        return ventaService.eliminarVenta(idVenta);
    }
    
}
