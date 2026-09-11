package com.duocuc.pedidosBackend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;


import com.duocuc.pedidosBackend.model.entities.Productos360;


public interface ProductosRepositories extends JpaRepository<Productos360, Integer> {
    
}
