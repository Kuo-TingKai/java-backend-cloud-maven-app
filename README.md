# Cloud Strategy Project

This is a comprehensive Spring Boot-based cloud strategy project that demonstrates advanced enterprise-level development skills including distributed systems, high concurrency, microservices, and cloud-native technologies.

## Project Structure

```
cloud-strategy/
├── src/
│   ├── main/
│   │   ├── java/org/example/
│   │   │   ├── CloudStrategyApplication.java     # Spring Boot main startup class
│   │   │   ├── HelloController.java              # Basic REST controller
│   │   │   ├── AdvancedController.java           # Advanced API endpoints
│   │   │   ├── entity/
│   │   │   │   └── User.java                     # JPA entity with optimized indexes
│   │   │   ├── document/
│   │   │   │   └── Product.java                  # MongoDB document with text indexes
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java           # JPA repository with custom queries
│   │   │   │   └── ProductRepository.java        # MongoDB repository with aggregation
│   │   │   ├── service/
│   │   │   │   ├── RedisService.java             # Redis data structures & operations
│   │   │   │   ├── KafkaService.java             # Kafka message queue operations
│   │   │   │   ├── WebCrawlerService.java        # Multi-threaded web crawling
│   │   │   │   └── AwsService.java               # AWS S3 operations
│   │   │   └── config/
│   │   │       ├── RedisConfig.java              # Redis configuration
│   │   │       ├── AwsConfig.java                # AWS configuration
│   │   │       ├── RestTemplateConfig.java       # RestTemplate configuration
│   │   │       └── WebSocketConfig.java          # WebSocket configuration
│   │   └── resources/
│   │       └── application.properties            # Comprehensive configuration
│   └── test/
├── pom.xml                                       # Maven configuration with all dependencies
├── Dockerfile                                    # Container configuration
├── docker-compose.yml                            # Multi-service deployment
└── prometheus.yml                                # Monitoring configuration
```

## Technology Stack

### Core Framework
- **Spring Boot 3.3.0** - Main framework with latest features
- **Spring Web** - REST API support with advanced endpoints
- **Spring Data JPA** - Relational database support with query optimization
- **Spring Data MongoDB** - NoSQL document database support
- **Spring Data Redis** - In-memory cache and session management
- **Spring WebSocket** - Real-time communication support

### Message Queues & Stream Processing
- **Apache Kafka** - High-throughput message streaming
- **RabbitMQ** - Reliable message queuing
- **Spring AMQP** - Advanced message queuing protocol

### Cloud Services
- **AWS SDK v2** - Amazon Web Services integration
- **AWS S3** - Object storage with presigned URLs
- **AWS CloudWatch** - Monitoring and logging

### Data Processing & Crawling
- **Jsoup** - HTML parsing and web scraping
- **Apache HttpClient** - HTTP client for web crawling
- **Multi-threading** - Concurrent processing with ExecutorService

### Monitoring & Observability
- **Spring Actuator** - Application monitoring endpoints
- **Micrometer** - Metrics collection
- **Prometheus** - Time-series metrics

### Database Support
- **MySQL** - Primary relational database
- **MongoDB** - Document database for flexible schemas
- **Redis** - Cache and session storage
- **H2** - Embedded database for testing

## Requirements

- Java 21+
- Maven 3.6+
- Redis (optional, for caching and distributed locks)
- MongoDB (optional, for document storage)
- Kafka (optional, for message streaming)
- AWS Account (optional, for S3 operations)

## How to Run

### 1. Compile the project
```bash
mvn clean compile
```

### 2. Run the project
```bash
mvn spring-boot:run
```

Or

```bash
mvn clean package
java -jar target/cloud-strategy-1.0-SNAPSHOT.jar
```

### 3. Access the application
- **Base URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/api/v1/health
- **Actuator**: http://localhost:8080/actuator

## Available APIs

### ✅ Currently Working APIs

#### 1. Basic Health Check
```bash
# Basic hello endpoint
GET http://localhost:8080/hello
# Response: "Hello, Spring Boot!"

# Comprehensive health check
GET http://localhost:8080/api/v1/health
# Response: Application status and available services
```

#### 2. Web Crawler Operations
```bash
# Extract links from a webpage
POST http://localhost:8080/api/v1/crawler/extract
Content-Type: application/json

{
  "url": "https://example.com",
  "operation": "links"
}

# Extract images from a webpage
POST http://localhost:8080/api/v1/crawler/extract
Content-Type: application/json

{
  "url": "https://example.com",
  "operation": "images"
}

# Extract text content from a webpage
POST http://localhost:8080/api/v1/crawler/extract
Content-Type: application/json

{
  "url": "https://example.com",
  "operation": "text"
}

# Extract metadata from a webpage
POST http://localhost:8080/api/v1/crawler/extract
Content-Type: application/json

{
  "url": "https://example.com",
  "operation": "metadata"
}
```

#### 3. AWS S3 Operations (Requires AWS Configuration)
```bash
# Upload file to S3
POST http://localhost:8080/api/v1/aws/upload
Content-Type: application/json

{
  "key": "test.txt",
  "content": "Hello World"
}

# Download file from S3
GET http://localhost:8080/api/v1/aws/download/test.txt

# Upload to specific bucket
POST http://localhost:8080/api/v1/aws/upload
Content-Type: application/json

{
  "key": "test.txt",
  "content": "Hello World",
  "bucket": "my-bucket"
}
```

#### 4. WebSocket Operations (Requires Frontend)
```bash
# Send WebSocket message
POST http://localhost:8080/api/v1/websocket/send
Content-Type: application/json

{
  "destination": "/topic/notifications",
  "message": "Hello WebSocket"
}
```

### ⚠️ Partially Implemented APIs (Require Service Configuration)

#### 1. Redis Operations (Requires Redis Server)
```bash
# Set Redis value
POST http://localhost:8080/api/v1/redis/set
Content-Type: application/json

{
  "key": "test",
  "value": "hello"
}

# Get Redis value
GET http://localhost:8080/api/v1/redis/get/test

# Set cache with TTL
POST http://localhost:8080/api/v1/redis/cache
Content-Type: application/json

{
  "key": "user:123",
  "value": "John Doe",
  "ttl": 300
}

# Rate limiting check
POST http://localhost:8080/api/v1/rate-limit/check
Content-Type: application/json

{
  "key": "user:123",
  "maxRequests": 10,
  "windowSeconds": 60
}

# Session management
POST http://localhost:8080/api/v1/session/set
Content-Type: application/json

{
  "sessionId": "abc123",
  "sessionData": {
    "userId": "123",
    "username": "john"
  },
  "ttl": 3600
}

GET http://localhost:8080/api/v1/session/get/abc123
```

#### 2. Kafka Operations (Requires Kafka Server)
```bash
# Send message to Kafka
POST http://localhost:8080/api/v1/kafka/send
Content-Type: application/json

{
  "topic": "user-events",
  "message": "User logged in",
  "key": "user123"
}

# Send object as JSON
POST http://localhost:8080/api/v1/kafka/send
Content-Type: application/json

{
  "topic": "orders",
  "message": {
    "orderId": "12345",
    "userId": "user123",
    "amount": 99.99
  }
}
```

### ❌ Not Yet Implemented APIs

#### 1. Database Operations
- JPA User entity operations (CRUD)
- MongoDB Product document operations
- Custom repository queries
- Database migrations

#### 2. RabbitMQ Operations
- Message publishing
- Message consumption
- Queue management

#### 3. Advanced Features
- Distributed locking (Redisson removed)
- Advanced caching strategies
- Database connection pooling
- Performance monitoring

## Configuration

### Enable Redis (Optional)
Edit `src/main/resources/application.properties`:
```properties
# Redis Configuration
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.password=
spring.data.redis.database=0
```

### Enable Kafka (Optional)
Edit `src/main/resources/application.properties`:
```properties
# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=cloud-strategy-group
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

### Enable AWS S3 (Optional)
Edit `src/main/resources/application.properties`:
```properties
# AWS Configuration
aws.accessKeyId=your-access-key
aws.secretKey=your-secret-key
aws.region=us-east-1
aws.s3.bucket=your-bucket-name
```

### Enable MongoDB (Optional)
Edit `src/main/resources/application.properties`:
```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/cloud_strategy
```

### Enable MySQL (Optional)
Edit `src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/cloud_strategy
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Testing Examples

### Using curl

#### 1. Health Check
```bash
curl http://localhost:8080/api/v1/health
```

#### 2. Web Crawler
```bash
curl -X POST http://localhost:8080/api/v1/crawler/extract \
  -H "Content-Type: application/json" \
  -d '{
    "url": "https://example.com",
    "operation": "links"
  }'
```

#### 3. AWS S3 (if configured)
```bash
curl -X POST http://localhost:8080/api/v1/aws/upload \
  -H "Content-Type: application/json" \
  -d '{
    "key": "test.txt",
    "content": "Hello World"
  }'
```

### Using Postman or Similar Tools

1. Import the following collection:
```json
{
  "info": {
    "name": "Cloud Strategy API",
    "description": "API collection for Cloud Strategy project"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/v1/health"
      }
    },
    {
      "name": "Web Crawler - Extract Links",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/api/v1/crawler/extract",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"url\": \"https://example.com\",\n  \"operation\": \"links\"\n}"
        }
      }
    }
  ]
}
```

## Docker Deployment

### Using Docker Compose
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f cloud-strategy

# Stop all services
docker-compose down
```

### Manual Docker Build
```bash
# Build image
docker build -t cloud-strategy .

# Run container
docker run -p 8080:8080 cloud-strategy
```

## Monitoring

### Actuator Endpoints
- `/actuator/health` - Application health
- `/actuator/info` - Application information
- `/actuator/metrics` - Performance metrics
- `/actuator/prometheus` - Prometheus metrics

### Prometheus & Grafana
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (admin/admin123)

## Development Notes

### Current Status
- ✅ Basic Spring Boot application
- ✅ Web crawler functionality
- ✅ AWS S3 integration (when configured)
- ✅ WebSocket support
- ✅ Health check endpoints
- ⚠️ Redis operations (requires Redis server)
- ⚠️ Kafka operations (requires Kafka server)
- ❌ Database operations (not implemented)
- ❌ Distributed locking (removed)

### Next Steps for Full Implementation
1. **Database Integration**: Implement JPA and MongoDB repositories
2. **Redis Setup**: Configure Redis server and test caching
3. **Kafka Setup**: Configure Kafka server and test messaging
4. **RabbitMQ**: Implement message queuing
5. **Advanced Features**: Add distributed locking, advanced caching
6. **Frontend**: Create WebSocket client for real-time features
7. **Testing**: Add comprehensive unit and integration tests

## Troubleshooting

### Common Issues

1. **Port 8080 already in use**
   ```bash
   # Change port in application.properties
   server.port=8081
   ```

2. **Redis connection failed**
   - Install and start Redis server
   - Or comment out Redis configuration

3. **Kafka connection failed**
   - Install and start Kafka server
   - Or comment out Kafka configuration

4. **AWS S3 access denied**
   - Verify AWS credentials
   - Check IAM permissions

### Logs
```bash
# View application logs
tail -f logs/application.log

# View Docker logs
docker-compose logs -f cloud-strategy
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License. 