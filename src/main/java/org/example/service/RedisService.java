package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "spring.data.redis.host")
public class RedisService {
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;
    
    // String operations
    public void setString(String key, String value) {
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(key, value);
        }
    }
    
    public void setStringWithExpiry(String key, String value, Duration expiry) {
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(key, value, expiry);
        }
    }
    
    public String getString(String key) {
        if (redisTemplate != null) {
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? value.toString() : null;
        }
        return null;
    }
    
    // Hash operations
    public void setHash(String key, String field, Object value) {
        if (redisTemplate != null) {
            redisTemplate.opsForHash().put(key, field, value);
        }
    }
    
    public void setHashAll(String key, Map<String, Object> map) {
        if (redisTemplate != null) {
            redisTemplate.opsForHash().putAll(key, map);
        }
    }
    
    public Object getHash(String key, String field) {
        if (redisTemplate != null) {
            return redisTemplate.opsForHash().get(key, field);
        }
        return null;
    }
    
    public Map<Object, Object> getHashAll(String key) {
        if (redisTemplate != null) {
            return redisTemplate.opsForHash().entries(key);
        }
        return Map.of();
    }
    
    // List operations
    public void pushToList(String key, Object value) {
        if (redisTemplate != null) {
            redisTemplate.opsForList().rightPush(key, value);
        }
    }
    
    public void pushToListLeft(String key, Object value) {
        if (redisTemplate != null) {
            redisTemplate.opsForList().leftPush(key, value);
        }
    }
    
    public Object popFromList(String key) {
        if (redisTemplate != null) {
            return redisTemplate.opsForList().leftPop(key);
        }
        return null;
    }
    
    public List<Object> getListRange(String key, long start, long end) {
        if (redisTemplate != null) {
            return redisTemplate.opsForList().range(key, start, end);
        }
        return List.of();
    }
    
    // Set operations
    public void addToSet(String key, Object... values) {
        if (redisTemplate != null) {
            redisTemplate.opsForSet().add(key, values);
        }
    }
    
    public Set<Object> getSetMembers(String key) {
        if (redisTemplate != null) {
            return redisTemplate.opsForSet().members(key);
        }
        return Set.of();
    }
    
    public boolean isSetMember(String key, Object value) {
        if (redisTemplate != null) {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        }
        return false;
    }
    
    // Sorted Set operations
    public void addToSortedSet(String key, Object value, double score) {
        if (redisTemplate != null) {
            redisTemplate.opsForZSet().add(key, value, score);
        }
    }
    
    public Set<Object> getSortedSetRange(String key, long start, long end) {
        if (redisTemplate != null) {
            return redisTemplate.opsForZSet().range(key, start, end);
        }
        return Set.of();
    }
    
    public Set<Object> getSortedSetRangeByScore(String key, double min, double max) {
        if (redisTemplate != null) {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        }
        return Set.of();
    }
    
    // Key operations
    public boolean exists(String key) {
        if (redisTemplate != null) {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        }
        return false;
    }
    
    public void delete(String key) {
        if (redisTemplate != null) {
            redisTemplate.delete(key);
        }
    }
    
    public void expire(String key, Duration duration) {
        if (redisTemplate != null) {
            redisTemplate.expire(key, duration);
        }
    }
    
    public Long getExpire(String key) {
        if (redisTemplate != null) {
            return redisTemplate.getExpire(key);
        }
        return null;
    }
    
    // Atomic operations with Lua script
    public Long incrementAtomic(String key, long delta) {
        if (redisTemplate != null) {
            return redisTemplate.opsForValue().increment(key, delta);
        }
        return null;
    }
    
    public Boolean setIfAbsent(String key, String value, Duration timeout) {
        if (redisTemplate != null) {
            return redisTemplate.opsForValue().setIfAbsent(key, value, timeout);
        }
        return false;
    }
    
    // Rate limiting with sliding window
    public boolean isRateLimited(String key, int maxRequests, Duration window) {
        if (redisTemplate == null) {
            return false; // Allow all requests if Redis is not available
        }
        
        String script = 
            "local current = redis.call('ZCARD', KEYS[1]) " +
            "if current >= tonumber(ARGV[1]) then " +
            "  return 0 " +
            "end " +
            "redis.call('ZADD', KEYS[1], ARGV[2], ARGV[3]) " +
            "redis.call('EXPIRE', KEYS[1], ARGV[4]) " +
            "return 1";
        
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(script);
        redisScript.setResultType(Long.class);
        
        long now = System.currentTimeMillis();
        long windowSeconds = window.getSeconds();
        
        Long result = redisTemplate.execute(redisScript, 
            List.of(key), 
            String.valueOf(maxRequests), 
            String.valueOf(now), 
            String.valueOf(now), 
            String.valueOf(windowSeconds));
        
        return result != null && result == 1;
    }
    
    // Cache operations
    public void setCache(String key, Object value, Duration ttl) {
        if (redisTemplate != null) {
            redisTemplate.opsForValue().set(key, value, ttl);
        }
    }
    
    public Object getCache(String key) {
        if (redisTemplate != null) {
            return redisTemplate.opsForValue().get(key);
        }
        return null;
    }
    
    // Session management
    public void setSession(String sessionId, Map<String, Object> sessionData, Duration ttl) {
        if (redisTemplate != null) {
            setHashAll("session:" + sessionId, sessionData);
            expire("session:" + sessionId, ttl);
        }
    }
    
    public Map<Object, Object> getSession(String sessionId) {
        if (redisTemplate != null) {
            return getHashAll("session:" + sessionId);
        }
        return Map.of();
    }
    
    public void deleteSession(String sessionId) {
        if (redisTemplate != null) {
            delete("session:" + sessionId);
        }
    }
} 