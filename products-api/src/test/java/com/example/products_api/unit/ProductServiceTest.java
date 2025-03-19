package com.example.products_api.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.example.products_api.entity.ProductEntity;
import com.example.products_api.exception.ProductNotFoundException;
import com.example.products_api.repository.ProductRepository;
import com.example.products_api.service.ProductService;
import com.example.products_api.utils.PagedResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProducts() {
        // ARRANGE
        ProductEntity product = new ProductEntity("Laptop", "Gaming Laptop", 1500.0);
        Page<ProductEntity> mockPage = new PageImpl<>(List.of(product));

        when(productRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        // ACT
        PagedResponse<ProductEntity> response = productService.getProducts(1, 10, "name", "asc");

        // ASSERT
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).getName()).isEqualTo("Laptop");
        assertThat(response.totalElements()).isEqualTo(1);
        verify(productRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testCreateProduct() {
        // ARRANGE
        ProductEntity product = new ProductEntity("Tablet", "Android Tablet", 300.0);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(product);

        // ACT
        ProductEntity savedProduct = productService.createProduct(product);

        // ASSERT
        assertThat(savedProduct.getName()).isEqualTo("Tablet");
        assertThat(savedProduct.getPrice()).isEqualTo(300.0);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testGetProductById_Found() {
        // ARRANGE
        ProductEntity product = new ProductEntity("Phone", "Smartphone", 800.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // ACT
        ProductEntity foundProduct = productService.getProductById(1L);

        // ASSERT
        assertThat(foundProduct.getName()).isEqualTo("Phone");
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // ARRANGE
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT / ASSERT
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateProduct_Found() {
        // ARRANGE
        ProductEntity existingProduct = new ProductEntity("Phone", "Smartphone", 800.0);
        ProductEntity updatedProduct = new ProductEntity("Updated Phone", "Updated Smartphone", 1000.0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(ProductEntity.class))).thenReturn(updatedProduct);

        // ACT
        ProductEntity result = productService.updateProduct(1L, updatedProduct);

        // ASSERT
        assertThat(result.getName()).isEqualTo("Updated Phone");
        assertThat(result.getPrice()).isEqualTo(1000.0);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(existingProduct);
    }

    @Test
    void testUpdateProduct_NotFound() {
        // ARRANGE
        ProductEntity updatedProduct = new ProductEntity("Updated Phone", "Updated Smartphone", 1000.0);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT / ASSERT
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(1L, updatedProduct));
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteProduct_Found() {
        // ARRANGE
        ProductEntity product = new ProductEntity("Tablet", "Android Tablet", 300.0);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        // ACT
        productService.deleteProduct(1L);

        // ASSERT
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // ARRANGE
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // ACT / ASSERT
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, times(1)).findById(1L);
    }
}