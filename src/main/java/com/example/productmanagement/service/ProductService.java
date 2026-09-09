package com.example.productmanagement.service;

import com.example.productmanagement.exception.ResourceNotFoundException;
import com.example.productmanagement.model.Product;
import com.example.productmanagement.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Cacheable(value = "products")
    public List<Product> getAllProducts() {
        log.info("Fetching all products from DB (Cache Miss)");
        return productRepository.findAll();
    }

    @Cacheable(value = "product", key = "#id")
    public Product getProductById(int id) {
        log.info("Fetching product {} from DB (Cache Miss)", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product createProduct(Product product) {
        log.info("Creating new product: {}", product.getName());
        product.setCreatedAt(new Date());
        return productRepository.save(product);
    }

    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public Product updateProduct(int id, Product productDetails) {
        log.info("Updating product id: {}", id);
        Product product = getProductById(id);
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        return productRepository.save(product);
    }

    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public void deleteProduct(int id) {
        log.info("Deleting product id: {}", id);
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public List<Product> searchProducts(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Searching products with criteria - name: {}, minPrice: {}, maxPrice: {}", name, minPrice, maxPrice);
        return productRepository.searchProducts(name, minPrice, maxPrice);
    }

    // Asynchronous Database Processing Contoh
    @Async
    public CompletableFuture<List<Product>> getAllProductsAsync() {
        log.info("Executing async product retrieval");
        return CompletableFuture.completedFuture(productRepository.findAll());
    }
}