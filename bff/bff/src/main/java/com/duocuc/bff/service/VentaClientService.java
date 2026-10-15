package com.duocuc.bff.service;

import com.duocuc.bff.dto.CrearVentaDto;
import com.duocuc.bff.dto.VentaDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VentaClientService {

    private final RestClient restClient;

    public VentaClientService(@Value("${microservice.ventas-backend.url:http://localhost:6767}") String ventasUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(ventasUrl)
                .build();
    }

    public List<VentaDto> obtenerVentas() {
        return restClient.get()
                .uri("/ventas")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<List<VentaDto>>() {});
    }

    public VentaDto obtenerVentaPorId(int idVenta) {
        return restClient.get()
                .uri("/ventas/{idVenta}", idVenta)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(VentaDto.class);
    }

    public VentaDto crearVenta(CrearVentaDto crearVentaDto) {
        try {
            return restClient.post()
                    .uri("/ventas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(crearVentaDto)
                    .retrieve()
                    .body(VentaDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El producto con ID " + crearVentaDto.getProducto_id_producto() + " no fue encontrado en el catálogo.");
        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(HttpStatus.valueOf(e.getStatusCode().value()),
                    e.getResponseBodyAsString());
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo conectar con el microservicio de ventas: " + e.getMessage());
        }
    }
}
