package org.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnProperty(name = "aws.region")
public class AwsService {
    
    @Autowired(required = false)
    private S3Client s3Client;
    
    @Autowired(required = false)
    private S3Presigner s3Presigner;
    
    @Value("${aws.s3.bucket:}")
    private String defaultBucket;
    
    public boolean uploadFile(String bucketName, String key, byte[] data) {
        if (s3Client == null) {
            return false;
        }
        
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }
    
    public boolean uploadFile(String bucketName, String key, InputStream inputStream) {
        if (s3Client == null) {
            return false;
        }
        
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, -1));
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }
    
    public boolean uploadFile(String bucketName, String key, String content) {
        if (s3Client == null) {
            return false;
        }
        
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }
    
    public byte[] downloadFile(String bucketName, String key) {
        if (s3Client == null) {
            return new byte[0];
        }
        
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from S3", e);
        }
    }
    
    public String downloadFileAsString(String bucketName, String key) {
        if (s3Client == null) {
            return "";
        }
        
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            return s3Client.getObjectAsBytes(getObjectRequest).asUtf8String();
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from S3", e);
        }
    }
    
    public boolean deleteFile(String bucketName, String key) {
        if (s3Client == null) {
            return false;
        }
        
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            s3Client.deleteObject(deleteObjectRequest);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }
    
    public boolean fileExists(String bucketName, String key) {
        if (s3Client == null) {
            return false;
        }
        
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Failed to check file existence in S3", e);
        }
    }
    
    public String generatePresignedUrl(String bucketName, String key, Duration expiration) {
        if (s3Presigner == null) {
            return "";
        }
        
        try {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(expiration)
                .getObjectRequest(GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build())
                .build();
            
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
    
    public List<S3Object> listObjects(String bucketName, String prefix) {
        if (s3Client == null) {
            return List.of();
        }
        
        try {
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(prefix)
                .build();
            
            ListObjectsV2Response response = s3Client.listObjectsV2(listObjectsRequest);
            return response.contents();
        } catch (Exception e) {
            throw new RuntimeException("Failed to list objects in S3", e);
        }
    }
    
    public boolean copyObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {
        if (s3Client == null) {
            return false;
        }
        
        try {
            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(sourceBucket)
                .sourceKey(sourceKey)
                .destinationBucket(destinationBucket)
                .destinationKey(destinationKey)
                .build();
            
            s3Client.copyObject(copyObjectRequest);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy object in S3", e);
        }
    }
    
    public boolean setObjectMetadata(String bucketName, String key, Map<String, String> metadata) {
        if (s3Client == null) {
            return false;
        }
        
        try {
            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(bucketName)
                .sourceKey(key)
                .destinationBucket(bucketName)
                .destinationKey(key)
                .metadataDirective(MetadataDirective.REPLACE)
                .metadata(metadata)
                .build();
            
            s3Client.copyObject(copyObjectRequest);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to set object metadata in S3", e);
        }
    }
    
    public Map<String, String> getObjectMetadata(String bucketName, String key) {
        if (s3Client == null) {
            return Map.of();
        }
        
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            
            HeadObjectResponse response = s3Client.headObject(headObjectRequest);
            return response.metadata();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get object metadata from S3", e);
        }
    }
    
    // Convenience methods using default bucket
    public boolean uploadFile(String key, byte[] data) {
        return uploadFile(defaultBucket, key, data);
    }
    
    public boolean uploadFile(String key, InputStream inputStream) {
        return uploadFile(defaultBucket, key, inputStream);
    }
    
    public boolean uploadFile(String key, String content) {
        return uploadFile(defaultBucket, key, content);
    }
    
    public byte[] downloadFile(String key) {
        return downloadFile(defaultBucket, key);
    }
    
    public String downloadFileAsString(String key) {
        return downloadFileAsString(defaultBucket, key);
    }
    
    public boolean deleteFile(String key) {
        return deleteFile(defaultBucket, key);
    }
    
    public boolean fileExists(String key) {
        return fileExists(defaultBucket, key);
    }
    
    public String generatePresignedUrl(String key, Duration expiration) {
        return generatePresignedUrl(defaultBucket, key, expiration);
    }
    
    public List<S3Object> listObjects(String prefix) {
        return listObjects(defaultBucket, prefix);
    }
    
    public Map<String, String> getObjectMetadata(String key) {
        return getObjectMetadata(defaultBucket, key);
    }
    
    public String downloadJson(String key) {
        return downloadFileAsString(key);
    }
}