package com.duocuc.bff.controller;

import com.duocuc.bff.dto.CrearVentaDto;
import com.duocuc.bff.dto.VentaDto;
import com.duocuc.bff.service.VentaClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/ventas")
public class VentaBffController {

    private final VentaClientService ventaClientService;

    public VentaBffController(VentaClientService ventaClientService) {
        this.ventaClientService = ventaClientService;
    }

    @GetMapping
    public ResponseEntity<List<VentaDto>> obtenerVentas(@AuthenticationPrincipal Jwt jwt) {
        logUserClaims(jwt, "obtenerVentas");
        List<VentaDto> ventas = ventaClientService.obtenerVentas();
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<VentaDto> obtenerVentaPorId(@PathVariable int idVenta, @AuthenticationPrincipal Jwt jwt) {
        logUserClaims(jwt, "obtenerVentaPorId");
        VentaDto venta = ventaClientService.obtenerVentaPorId(idVenta);
        return ResponseEntity.ok(venta);
    }

    @PostMapping
    public ResponseEntity<VentaDto> crearVenta(@RequestBody CrearVentaDto crearVentaDto,
                                               @AuthenticationPrincipal Jwt jwt) {
        logUserClaims(jwt, "crearVenta");
        // Si el cliente no indicó un nombre, utilizar el del usuario autenticado en MSAL
        if (jwt != null && (crearVentaDto.getNombreCliente() == null || crearVentaDto.getNombreCliente().trim().isEmpty())) {
            String username = jwt.getClaimAsString("name");
            if (username == null) {
                username = jwt.getClaimAsString("preferred_username");
            }
            if (username != null) {
                crearVentaDto.setNombreCliente(username);
            }
        }
        VentaDto nuevaVenta = ventaClientService.crearVenta(crearVentaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaVenta);
    }

    private void logUserClaims(Jwt jwt, String action) {
        if (jwt != null) {
            String username = jwt.getClaimAsString("preferred_username");
            if (username == null) {
                username = jwt.getClaimAsString("name");
            }
            if (username == null) {
                username = jwt.getSubject();
            }
            List<String> roles = jwt.getClaimAsStringList("roles");
            String scopes = jwt.getClaimAsString("scp");

            log.info("[BFF Auth Log - Ventas] Acción: {} | Usuario: {} | Roles: {} | Scopes: {}",
                    action, username, roles, scopes);
        }
    }
}
