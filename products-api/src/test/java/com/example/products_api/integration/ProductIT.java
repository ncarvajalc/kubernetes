package com.example.products_api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.example.products_api.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = com.example.products_api.ProductsApiApplication.class)
@ActiveProfiles("test")
class ProductIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateAndGetProduct() {
        ProductDTO newProduct = new ProductDTO(null, "Tablet", "Android Tablet", 300.0);
        ResponseEntity<ProductDTO> createResponse = restTemplate.postForEntity(
                "/api/products", newProduct, ProductDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(CREATED);
        ProductDTO createdProduct = createResponse.getBody();
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo("Tablet");

        ResponseEntity<ProductDTO> getResponse = restTemplate.getForEntity(
                "/api/products/" + createdProduct.getId(), ProductDTO.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(OK);
        ProductDTO fetchedProduct = getResponse.getBody();
        assertThat(fetchedProduct).isNotNull();
        assertThat(fetchedProduct.getName()).isEqualTo("Tablet");
    }

    @Test
    void testUpdateProduct() {
        ProductDTO newProduct = new ProductDTO(null, "Tablet", "Android Tablet", 300.0);
        ResponseEntity<ProductDTO> createResponse = restTemplate.postForEntity(
                "/api/products", newProduct, ProductDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(CREATED);
        ProductDTO createdProduct = createResponse.getBody();
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo("Tablet");

        ProductDTO updatedProduct = new ProductDTO(createdProduct.getId(), "iPad", "iOS Tablet", 500.0);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductDTO> request = new HttpEntity<>(updatedProduct, headers);

        ResponseEntity<ProductDTO> updateResponse = restTemplate.exchange(
                "/api/products/" + createdProduct.getId(), HttpMethod.PUT, request, ProductDTO.class);

        assertThat(updateResponse.getStatusCode()).isEqualTo(OK);
        ProductDTO modifiedProduct = updateResponse.getBody();
        assertThat(modifiedProduct).isNotNull();
        assertThat(modifiedProduct.getName()).isEqualTo("iPad");
    }

    @Test
    void testDeleteProduct() {
        ProductDTO newProduct = new ProductDTO(null, "Tablet", "Android Tablet", 300.0);
        ResponseEntity<ProductDTO> createResponse = restTemplate.postForEntity(
                "/api/products", newProduct, ProductDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(CREATED);
        ProductDTO createdProduct = createResponse.getBody();
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo("Tablet");

        restTemplate.delete("/api/products/" + createdProduct.getId());

        ResponseEntity<ProductDTO> getResponse = restTemplate.getForEntity(
                "/api/products/" + createdProduct.getId(), ProductDTO.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}