package org.example.repository;

import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Basic CRUD operations with optimized queries
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByIsActiveTrue();
    
    // Custom query with JOIN for complex scenarios
    @Query("SELECT u FROM User u WHERE u.isActive = true AND u.createdAt >= :startDate")
    List<User> findActiveUsersCreatedAfter(@Param("startDate") java.time.LocalDateTime startDate);
    
    // Native query for complex operations
    @Query(value = "SELECT * FROM users WHERE is_active = true AND created_at >= :startDate ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<User> findRecentActiveUsers(@Param("startDate") java.time.LocalDateTime startDate, @Param("limit") int limit);
    
    // Count query for statistics
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();
    
    // Check if user exists (for validation)
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // Find users by multiple criteria
    List<User> findByUsernameContainingIgnoreCaseAndIsActiveTrue(String username);
    
    // Custom query for user search with pagination
    @Query("SELECT u FROM User u WHERE " +
           "(:username IS NULL OR u.username LIKE %:username%) AND " +
           "(:email IS NULL OR u.email LIKE %:email%) AND " +
           "u.isActive = true")
    List<User> searchUsers(@Param("username") String username, @Param("email") String email);
} 