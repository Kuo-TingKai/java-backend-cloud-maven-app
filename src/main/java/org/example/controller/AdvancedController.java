package org.example.controller;

import org.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class AdvancedController {
    
    @Autowired(required = false)
    private RedisService redisService;
    
    @Autowired(required = false)
    private KafkaService kafkaService;
    
    @Autowired(required = false)
    private WebCrawlerService webCrawlerService;
    
    @Autowired(required = false)
    private AwsService awsService;
    
    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;
    
    // Redis Operations
    @PostMapping("/redis/set")
    public ResponseEntity<Map<String, Object>> setRedisValue(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        if (redisService != null) {
            String key = request.get("key");
            String value = request.get("value");
            redisService.setString(key, value);
            response.put("success", true);
            response.put("message", "Value set successfully");
        } else {
            response.put("success", false);
            response.put("message", "Redis service not available");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/redis/get/{key}")
    public ResponseEntity<Map<String, Object>> getRedisValue(@PathVariable String key) {
        Map<String, Object> response = new HashMap<>();
        
        if (redisService != null) {
            String value = redisService.getString(key);
            response.put("success", true);
            response.put("key", key);
            response.put("value", value);
        } else {
            response.put("success", false);
            response.put("message", "Redis service not available");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/redis/cache")
    public ResponseEntity<Map<String, Object>> setCache(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        if (redisService != null) {
            String key = (String) request.get("key");
            Object value = request.get("value");
            long ttl = ((Number) request.get("ttl")).longValue();
            redisService.setCache(key, value, Duration.ofSeconds(ttl));
            response.put("success", true);
            response.put("message", "Cache set successfully");
        } else {
            response.put("success", false);
            response.put("message", "Redis service not available");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // Kafka Operations
    @PostMapping("/kafka/send")
    public ResponseEntity<Map<String, Object>> sendKafkaMessage(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        if (kafkaService != null) {
            String topic = (String) request.get("topic");
            String message = (String) request.get("message");
            String key = (String) request.get("key");
            
            CompletableFuture<?> future;
            if (key != null) {
                future = kafkaService.sendMessage(topic, key, message);
            } else {
                future = kafkaService.sendMessage(topic, message);
            }
            
            response.put("success", true);
            response.put("message", "Message sent to Kafka");
            response.put("topic", topic);
        } else {
            response.put("success", false);
            response.put("message", "Kafka service not available");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // Web Crawler Operations
    @PostMapping("/crawler/extract")
    public ResponseEntity<Map<String, Object>> crawlUrl(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        if (webCrawlerService != null) {
            try {
                String url = request.get("url");
                String operation = request.get("operation");
                
                switch (operation) {
                    case "links":
                        List<String> links = webCrawlerService.extractLinks(url);
                        response.put("data", links);
                        break;
                    case "images":
                        List<String> images = webCrawlerService.extractImages(url);
                        response.put("data", images);
                        break;
                    case "text":
                        String text = webCrawlerService.extractText(url);
                        response.put("data", text);
                        break;
                    case "metadata":
                        Map<String, String> metadata = webCrawlerService.extractMetaData(url);
                        response.put("data", metadata);
                        break;
                    default:
                        response.put("error", "Unknown operation: " + operation);
                        return ResponseEntity.badRequest().body(response);
                }
                
                response.put("success", true);
                response.put("url", url);
            } catch (Exception e) {
                response.put("success", false);
                response.put("error", e.getMessage());
            }
        } else {
            response.put("success", false);
            response.put("message", "Web crawler service not available");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // AWS S3 Operations
    @PostMapping("/aws/upload")
    public ResponseEntity<Map<String, Object>> uploadToS3(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        if (awsService != null) {
            try {
                String key = (String) request.get("key");
                String content = (String) request.get("content");
                String bucket = (String) request.get("bucket");
                
                boolean success;
                if (bucket != null) {
                    success = awsService.uploadFile(bucket, key, content);
                } else {
                    success = awsService.uploadFile(key, content);
                }
                
                response.put("success", success);
                response.put("key", key);
                response.put("message", success ? "File uploaded successfully" : "Upload failed");
            } catch (Exception e) {
                response.put("success", false);
                response.put("error", e.getMessage());
            }
        } else {
            response.put("success", false);
            response.put("message", "AWS service not available");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/aws/download/{key}")
    public ResponseEntity<Map<String, Object>> downloadFromS3(@PathVariable String key) {
        Map<String, Object> response = new HashMap<>();
        
        if (awsService != null) {
            try {
                String content = awsService.downloadFileAsString(key);
                response.put("success", true);
                response.put("key", key);
                response.put("content", content);
            } catch (Exception e) {
                response.put("success", false);
                response.put("error", e.getMessage());
            }
        } else {
            response.put("success", false);
            response.put("message", "AWS service not available");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // WebSocket Operations
    @PostMapping("/websocket/send")
    public ResponseEntity<Map<String, Object>> sendWebSocketMessage(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        if (messagingTemplate != null) {
            String destination = (String) request.get("destination");
            Object message = request.get("message");
            
            messagingTemplate.convertAndSend(destination, message);
            response.put("success", true);
            response.put("message", "WebSocket message sent");
            response.put("destination", destination);
        } else {
            response.put("success", false);
            response.put("message", "WebSocket service not available");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // Rate Limiting
    @PostMapping("/rate-limit/check")
    public ResponseEntity<Map<String, Object>> checkRateLimit(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        if (redisService != null) {
            String key = (String) request.get("key");
            int maxRequests = ((Number) request.get("maxRequests")).intValue();
            long windowSeconds = ((Number) request.get("windowSeconds")).longValue();
            
            boolean isLimited = redisService.isRateLimited(key, maxRequests, Duration.ofSeconds(windowSeconds));
            response.put("success", true);
            response.put("isLimited", isLimited);
            response.put("key", key);
        } else {
            response.put("success", false);
            response.put("message", "Rate limiting service not available");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // Session Management
    @PostMapping("/session/set")
    public ResponseEntity<Map<String, Object>> setSession(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        if (redisService != null) {
            String sessionId = (String) request.get("sessionId");
            @SuppressWarnings("unchecked")
            Map<String, Object> sessionData = (Map<String, Object>) request.get("sessionData");
            long ttl = ((Number) request.get("ttl")).longValue();
            
            redisService.setSession(sessionId, sessionData, Duration.ofSeconds(ttl));
            response.put("success", true);
            response.put("message", "Session set successfully");
        } else {
            response.put("success", false);
            response.put("message", "Session service not available");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/session/get/{sessionId}")
    public ResponseEntity<Map<String, Object>> getSession(@PathVariable String sessionId) {
        Map<String, Object> response = new HashMap<>();
        
        if (redisService != null) {
            Map<Object, Object> sessionData = redisService.getSession(sessionId);
            response.put("success", true);
            response.put("sessionId", sessionId);
            response.put("sessionData", sessionData);
        } else {
            response.put("success", false);
            response.put("message", "Session service not available");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // Health Check
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        response.put("services", Map.of(
            "redis", redisService != null,
            "kafka", kafkaService != null,
            "crawler", webCrawlerService != null,
            "aws", awsService != null,
            "websocket", messagingTemplate != null
        ));
        
        return ResponseEntity.ok(response);
    }
} 