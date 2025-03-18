package com.example.products_api.repository;

import org.springframework.stereotype.Repository;

import com.example.products_api.entity.ProductEntity;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}