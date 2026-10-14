package com.pedidos360.ventas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pedidos360.ventas.model.entities.Venta;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

}
