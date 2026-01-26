package com.app.backend.repository;

import com.app.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	boolean existsByNameIgnoreCase(String name);
}
