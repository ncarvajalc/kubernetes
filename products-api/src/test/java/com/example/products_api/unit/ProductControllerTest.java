package com.example.products_api.unit;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.products_api.controller.ProductController;
import com.example.products_api.dto.ProductDTO;
import com.example.products_api.entity.ProductEntity;
import com.example.products_api.service.ProductService;
import com.example.products_api.utils.PagedResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void testGetProducts() throws Exception {
        ProductEntity product = new ProductEntity("Laptop", "Gaming laptop", 1500.0);
        ProductDTO productDTO = new ProductDTO(1L, "Laptop", "Gaming laptop", 1500.0);

        when(productService.getProducts(1, 10, "name", "asc"))
                .thenReturn(new PagedResponse<>(List.of(product), 1, 1));
        when(modelMapper.map(any(ProductEntity.class), eq(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop"));
    }

    @Test
    void testGetProductById() throws Exception {
        ProductEntity product = new ProductEntity("Phone", "Smartphone", 800.0);
        ProductDTO productDTO = new ProductDTO(1L, "Phone", "Smartphone", 800.0);

        when(productService.getProductById(1L)).thenReturn(product);
        when(modelMapper.map(any(ProductEntity.class), eq(ProductDTO.class))).thenReturn(productDTO);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Phone"));
    }

    @Test
    void testCreateProduct() throws Exception {
        new ProductDTO(null, "Tablet", "Android Tablet", 300.0);
        ProductEntity createdProduct = new ProductEntity("Tablet", "Android Tablet", 300.0);
        ProductDTO outputDTO = new ProductDTO(1L, "Tablet", "Android Tablet", 300.0);

        when(modelMapper.map(any(ProductDTO.class), eq(ProductEntity.class))).thenReturn(createdProduct);
        when(productService.createProduct(any(ProductEntity.class))).thenReturn(createdProduct);
        when(modelMapper.map(any(ProductEntity.class), eq(ProductDTO.class))).thenReturn(outputDTO);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"Tablet\", \"description\": \"Android Tablet\", \"price\": 300.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tablet"));
    }
}