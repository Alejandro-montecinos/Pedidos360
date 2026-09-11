package com.duocuc.bff.service;

import com.duocuc.bff.dto.ActualizarProductoDto;
import com.duocuc.bff.dto.AgregarProductoDto;
import com.duocuc.bff.dto.ProductoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ProductoClientService {

    private final RestClient restClient;

    public ProductoClientService(@Value("${microservice.pedidos-backend.url:http://localhost:4545}") String backendUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(backendUrl)
                .build();
    }

    public List<ProductoDto> obtenerTodosLosProductos() {
        return restClient.get()
                .uri("/api/v1/productos")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ProductoDto>>() {});
    }

    public ProductoDto obtenerProductoPorId(int idP) {
        return restClient.get()
                .uri("/api/v1/productos/{idP}", idP)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ProductoDto.class);
    }

    public ProductoDto agregarProducto(AgregarProductoDto agregarProductoDto) {
        return restClient.post()
                .uri("/api/v1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(agregarProductoDto)
                .retrieve()
                .body(ProductoDto.class);
    }

    public ProductoDto actualizarProducto(int idP, ActualizarProductoDto actualizarProductoDto) {
        return restClient.put()
                .uri("/api/v1/productos/{idP}", idP)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(actualizarProductoDto)
                .retrieve()
                .body(ProductoDto.class);
    }

    public String eliminarProducto(int idP) {
        return restClient.delete()
                .uri("/api/v1/productos/{idP}", idP)
                .retrieve()
                .body(String.class);
    }
}
