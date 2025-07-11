package org.example.repository;

import org.example.document.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    // Basic CRUD operations
    List<Product> findByCategory(String category);
    
    List<Product> findByBrand(String brand);
    
    List<Product> findByIsActiveTrue();
    
    // Price range queries
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Product> findByPriceLessThan(BigDecimal price);
    
    // Text search using MongoDB text index
    @Query("{'$text': {'$search': ?0}}")
    List<Product> searchByText(String searchText);
    
    // Complex queries with multiple criteria
    @Query("{'category': ?0, 'price': {'$gte': ?1, '$lte': ?2}, 'isActive': true}")
    List<Product> findByCategoryAndPriceRange(String category, BigDecimal minPrice, BigDecimal maxPrice);
    
    // Aggregation-like queries
    @Query("{'tags': {'$in': ?0}, 'isActive': true}")
    List<Product> findByTags(List<String> tags);
    
    // Custom query with regex for partial matching
    @Query("{'name': {'$regex': ?0, '$options': 'i'}, 'isActive': true}")
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Query with nested object attributes
    @Query("{'attributes.color': ?0, 'isActive': true}")
    List<Product> findByAttributeColor(String color);
    
    // Count queries for statistics
    long countByCategory(String category);
    
    long countByIsActiveTrue();
    
    // Exists queries for validation
    boolean existsByProductCode(String productCode);
    
    // Find by product code
    Product findByProductCode(String productCode);
    
    // Custom query for product search with multiple criteria
    @Query("{'$and': [" +
           "{'$or': [{'name': {'$regex': ?0, '$options': 'i'}}, {'description': {'$regex': ?0, '$options': 'i'}}]}," +
           "{'category': ?1}," +
           "{'price': {'$gte': ?2, '$lte': ?3}}," +
           "{'isActive': true}" +
           "]}")
    List<Product> searchProducts(String searchText, String category, BigDecimal minPrice, BigDecimal maxPrice);
} 