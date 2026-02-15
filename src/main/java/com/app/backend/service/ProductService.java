package com.app.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.model.Product;
import com.app.backend.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;

	@Transactional(readOnly = true)
	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Product findById(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));
	}

	@Transactional
	public Product create(Product product) {
		if (productRepository.existsByNameIgnoreCase(product.getName())) {
			throw new IllegalArgumentException("Product with this name already exists");
		}
		normalizeTextFields(product);
		sanitizePrice(product);
		return productRepository.save(product);
	}

	@Transactional
	public Product update(Long id, Product updatedProduct) {
		Product existing = findById(id);
		if (!existing.getName().equalsIgnoreCase(updatedProduct.getName())
				&& productRepository.existsByNameIgnoreCase(updatedProduct.getName())) {
			throw new IllegalArgumentException("Product with this name already exists");
		}
		existing.setName(updatedProduct.getName());
		existing.setDescription(updatedProduct.getDescription());
		existing.setPrice(normalizePrice(updatedProduct.getPrice()));
		existing.setStock(updatedProduct.getStock());
		existing.setCategories(normalizeOptionalText(updatedProduct.getCategories()));
		existing.setTags(normalizeTags(updatedProduct.getTags()));
		existing.setFeatures(normalizeOptionalText(updatedProduct.getFeatures()));
		existing.setImageUrl(updatedProduct.getImageUrl());
		return productRepository.save(existing);
	}

	@Transactional
	public void delete(Long id) {
		Product existing = findById(id);
		productRepository.delete(existing);
	}

	private void sanitizePrice(Product product) {
		product.setPrice(normalizePrice(product.getPrice()));
	}

	private void normalizeTextFields(Product product) {
		product.setCategories(normalizeOptionalText(product.getCategories()));
		product.setTags(normalizeTags(product.getTags()));
		product.setFeatures(normalizeOptionalText(product.getFeatures()));
	}

	private String normalizeTags(String tags) {
		String normalized = normalizeOptionalText(tags);
		if (normalized == null) {
			return null;
		}
		return java.util.Arrays.stream(normalized.split(","))
				.map(String::trim)
				.filter(tag -> !tag.isEmpty())
				.distinct()
				.reduce((left, right) -> left + ", " + right)
				.orElse(null);
	}

	private String normalizeOptionalText(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private BigDecimal normalizePrice(BigDecimal price) {
		if (price == null) {
			throw new IllegalArgumentException("Price is required");
		}
		if (price.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Price cannot be negative");
		}
		return price.setScale(2, RoundingMode.HALF_UP);
	}
}
