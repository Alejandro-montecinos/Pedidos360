package com.duocuc.pedidosBackend.services;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.duocuc.pedidosBackend.model.entities.Productos360;
import com.duocuc.pedidosBackend.model.request.ActualizarMarca;
import com.duocuc.pedidosBackend.model.request.AgregarProducto;
import com.duocuc.pedidosBackend.repositories.ProductosRepositories;

@Service 
public class ProductosService {
    
    @Autowired 
    private ProductosRepositories productosRepositories;
    

    public List<Productos360> obtenerTodosLosProductos(){
        return productosRepositories.findAll();
    }

    public Productos360 obtenerProductosPorId(int idP){
        Productos360 productos360 = productosRepositories.findById(idP).orElse(null);
        if (productos360 == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado");
        }
        return productos360;
    }

    public Productos360 agregarProducto(AgregarProducto agregarProducto){
        Productos360 productos360n = new  Productos360();
        productos360n.setNombre(agregarProducto.getNombre());
        productos360n.setDescripcion(agregarProducto.getDescripcion());
        productos360n.setPrecio(agregarProducto.getPrecio());

        return productosRepositories.save(productos360n);
    }

    public String eliminarProducto (int idP){
        if (productosRepositories.existsById(idP)) {
            productosRepositories.deleteById(idP);
            return "Producto eliminado";
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado");
        }   
    }



    public Productos360 actualizarProducto(ActualizarMarca actualizarMarca, int idP){
        Productos360 productos360n = productosRepositories.findById(idP).orElse(null);
        if (productos360n == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Producto no encontrado");
        }else{
            productos360n.setNombre(actualizarMarca.getNombre());
            productos360n.setDescripcion(actualizarMarca.getDescripcion());
            productos360n.setPrecio(actualizarMarca.getPrecio());
            return productosRepositories.save(productos360n);
        }
        
    }
    

}
