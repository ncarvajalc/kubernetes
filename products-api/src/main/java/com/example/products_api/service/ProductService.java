package com.example.products_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.products_api.entity.ProductEntity;
import com.example.products_api.exception.ProductNotFoundException;
import com.example.products_api.repository.ProductRepository;
import com.example.products_api.utils.PagedResponse;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public PagedResponse<ProductEntity> getProducts(int page, int size, String sortBy, String sortDir) {
        log.info("Fetching products - page: {}, size: {}, sortBy: {}, sortDir: {}", page, size, sortBy, sortDir);

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
        Page<ProductEntity> products = productRepository.findAll(pageable);

        log.info("Fetched {} products", products);
        return new PagedResponse<>(products.getContent(), products.getTotalElements(), products.getTotalPages());
    }

    public ProductEntity createProduct(ProductEntity product) {
        log.info("Creating new product: {}", product);
        ProductEntity savedProduct = productRepository.save(product);
        log.info("Product created with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    public ProductEntity getProductById(Long id) {
        log.info("Fetching product with ID: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product with ID {} not found", id);
                    return new ProductNotFoundException(id);
                });
    }

    public ProductEntity updateProduct(Long id, ProductEntity product) {
        log.info("Updating product with ID: {}", id);
        return productRepository.findById(id)
                .map(existingProduct -> {
                    log.info("Existing product found: {}", existingProduct);
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setPrice(product.getPrice());
                    ProductEntity updatedProduct = productRepository.save(existingProduct);
                    log.info("Product updated: {}", updatedProduct);
                    return updatedProduct;
                })
                .orElseThrow(() -> {
                    log.error("Product with ID {} not found for update", id);
                    return new ProductNotFoundException(id);
                });
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product with ID {} not found for deletion", id);
                    return new ProductNotFoundException(id);
                });
        productRepository.delete(product);
        log.info("Product with ID {} deleted successfully", id);
    }
}