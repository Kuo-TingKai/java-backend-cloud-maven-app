package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsConfig {
    
    @Value("${aws.accessKeyId:}")
    private String accessKeyId;
    
    @Value("${aws.secretKey:}")
    private String secretKey;
    
    @Value("${aws.region:us-east-1}")
    private String region;
    
    @Bean
    public S3Client s3Client() {
        if (accessKeyId != null && !accessKeyId.isEmpty() && 
            secretKey != null && !secretKey.isEmpty()) {
            // Use provided credentials
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);
            return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
        } else {
            // Use default credential provider chain (IAM roles, environment variables, etc.)
            return S3Client.builder()
                .region(Region.of(region))
                .build();
        }
    }
    
    @Bean
    public S3Presigner s3Presigner() {
        if (accessKeyId != null && !accessKeyId.isEmpty() && 
            secretKey != null && !secretKey.isEmpty()) {
            // Use provided credentials
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretKey);
            return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
        } else {
            // Use default credential provider chain
            return S3Presigner.builder()
                .region(Region.of(region))
                .build();
        }
    }
} 