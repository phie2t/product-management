package com.example.productmanagement;

import com.example.productmanagement.controller.ProductApiController;
import com.example.productmanagement.model.Product;
import com.example.productmanagement.service.JwtService;
import com.example.productmanagement.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductApiController.class)
public class ProductApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser
    public void testGetAllProducts() throws Exception {
        Product p1 = new Product("Item 1", "Desc 1", new BigDecimal("100.00"));
        Product p2 = new Product("Item 2", "Desc 2", new BigDecimal("200.00"));

        Mockito.when(productService.getAllProducts()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Item 1"));
    }

    @Test
    @WithMockUser
    public void testCreateProduct() throws Exception {
        Product p = new Product("New Item", "Desc", new BigDecimal("150.00"));
        Mockito.when(productService.createProduct(Mockito.any(Product.class))).thenReturn(p);

        String jsonPayload = "{\"name\":\"New Item\",\"description\":\"Desc\",\"price\":150.00}";

        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Item"));
    }
}