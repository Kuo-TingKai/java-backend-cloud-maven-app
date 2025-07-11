package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KafkaService {
    
    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public CompletableFuture<SendResult<String, String>> sendMessage(String topic, String message) {
        if (kafkaTemplate != null) {
            return kafkaTemplate.send(topic, message);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    public CompletableFuture<SendResult<String, String>> sendMessage(String topic, String key, String message) {
        if (kafkaTemplate != null) {
            return kafkaTemplate.send(topic, key, message);
        }
        return CompletableFuture.completedFuture(null);
    }
    
    public CompletableFuture<SendResult<String, String>> sendMessage(String topic, Object message) {
        if (kafkaTemplate != null) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                return kafkaTemplate.send(topic, jsonMessage);
            } catch (Exception e) {
                CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
                future.completeExceptionally(e);
                return future;
            }
        }
        return CompletableFuture.completedFuture(null);
    }
    
    public CompletableFuture<SendResult<String, String>> sendMessage(String topic, String key, Object message) {
        if (kafkaTemplate != null) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                return kafkaTemplate.send(topic, key, jsonMessage);
            } catch (Exception e) {
                CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
                future.completeExceptionally(e);
                return future;
            }
        }
        return CompletableFuture.completedFuture(null);
    }
    
    // Send message synchronously
    public SendResult<String, String> sendMessageSync(String topic, String message) {
        if (kafkaTemplate != null) {
            try {
                return kafkaTemplate.send(topic, message).get();
            } catch (Exception e) {
                throw new RuntimeException("Failed to send message to Kafka", e);
            }
        }
        return null;
    }
    
    public SendResult<String, String> sendMessageSync(String topic, String key, String message) {
        if (kafkaTemplate != null) {
            try {
                return kafkaTemplate.send(topic, key, message).get();
            } catch (Exception e) {
                throw new RuntimeException("Failed to send message to Kafka", e);
            }
        }
        return null;
    }
    
    public SendResult<String, String> sendMessageSync(String topic, Object message) {
        if (kafkaTemplate != null) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                return kafkaTemplate.send(topic, jsonMessage).get();
            } catch (Exception e) {
                throw new RuntimeException("Failed to send message to Kafka", e);
            }
        }
        return null;
    }
    
    public SendResult<String, String> sendMessageSync(String topic, String key, Object message) {
        if (kafkaTemplate != null) {
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                return kafkaTemplate.send(topic, key, jsonMessage).get();
            } catch (Exception e) {
                throw new RuntimeException("Failed to send message to Kafka", e);
            }
        }
        return null;
    }
} 