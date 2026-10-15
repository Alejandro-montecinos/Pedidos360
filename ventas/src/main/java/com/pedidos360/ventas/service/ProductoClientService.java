package com.pedidos360.ventas.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import com.pedidos360.ventas.model.dto.ProductoDTO;

@Service
public class ProductoClientService {

    private final RestClient restClient;

    public ProductoClientService(@Value("${microservice.pedidos-backend.url:http://localhost:4545}") String backendUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(backendUrl)
                .build();
    }

    public ProductoDTO obtenerProductoPorId(int idP) {
        try {
            return restClient.get()
                    .uri("/api/v1/productos/{idP}", idP)
                    .retrieve()
                    .body(ProductoDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El producto con ID " + idP + " no fue encontrado en el catálogo.");
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode().value()),
                    "Error al consultar el producto: " + e.getStatusText());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el microservicio de pedidosBackend (puerto 4545): " + e.getMessage());
        }
    }
}
