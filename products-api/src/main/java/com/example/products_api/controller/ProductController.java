package com.example.products_api.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.products_api.dto.ProductDTO;
import com.example.products_api.entity.ProductEntity;
import com.example.products_api.service.ProductService;
import com.example.products_api.utils.PagedResponse;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ModelMapper modelMapper;

    public ProductController(ProductService productService, ModelMapper modelMapper) {
        this.productService = productService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<ProductDTO>> getProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PagedResponse<ProductDTO> productDTOs = productService.getProducts(page, size, sortBy, sortDir)
                .map(product -> modelMapper.map(product, ProductDTO.class));
        return ResponseEntity.ok(productDTOs);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO createProduct(@RequestBody ProductDTO productDTO) {
        ProductEntity createdProduct = productService.createProduct(modelMapper.map(productDTO, ProductEntity.class));
        return modelMapper.map(createdProduct, ProductDTO.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(modelMapper.map(productService.getProductById(id), ProductDTO.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        ProductEntity updatedProduct = productService.updateProduct(id,
                modelMapper.map(productDTO, ProductEntity.class));
        return ResponseEntity.ok(modelMapper.map(updatedProduct, ProductDTO.class));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
