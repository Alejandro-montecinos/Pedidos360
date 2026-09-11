package com.duocuc.bff.controller;

import com.duocuc.bff.dto.ActualizarProductoDto;
import com.duocuc.bff.dto.AgregarProductoDto;
import com.duocuc.bff.dto.ProductoDto;
import com.duocuc.bff.service.ProductoClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoBffController {

    private final ProductoClientService productoClientService;

    public ProductoBffController(ProductoClientService productoClientService) {
        this.productoClientService = productoClientService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDto>> obtenerTodosLosProductos(@AuthenticationPrincipal Jwt jwt) {
        logUserClaims(jwt, "obtenerTodosLosProductos");
        List<ProductoDto> productos = productoClientService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{idP}")
    public ResponseEntity<ProductoDto> obtenerProductoPorId(@PathVariable int idP, @AuthenticationPrincipal Jwt jwt) {
        logUserClaims(jwt, "obtenerProductoPorId");
        ProductoDto producto = productoClientService.obtenerProductoPorId(idP);
        return ResponseEntity.ok(producto);
    }

    @PostMapping
    public ResponseEntity<ProductoDto> agregarProducto(@RequestBody AgregarProductoDto agregarProductoDto,
                                                       @AuthenticationPrincipal Jwt jwt) {
        logUserClaims(jwt, "agregarProducto");
        ProductoDto nuevoProducto = productoClientService.agregarProducto(agregarProductoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @PutMapping("/{idP}")
    public ResponseEntity<ProductoDto> actualizarProducto(@PathVariable int idP,
                                                          @RequestBody ActualizarProductoDto actualizarProductoDto,
                                                          @AuthenticationPrincipal Jwt jwt) {
        logUserClaims(jwt, "actualizarProducto");
        ProductoDto productoActualizado = productoClientService.actualizarProducto(idP, actualizarProductoDto);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{idP}")
    public ResponseEntity<String> eliminarProducto(@PathVariable int idP, @AuthenticationPrincipal Jwt jwt) {
        logUserClaims(jwt, "eliminarProducto");
        String respuesta = productoClientService.eliminarProducto(idP);
        return ResponseEntity.ok(respuesta);
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

            log.info("[BFF Auth Log] Acción: {} | Usuario: {} | Roles: {} | Scopes: {}",
                    action, username, roles, scopes);
        }
    }
}
