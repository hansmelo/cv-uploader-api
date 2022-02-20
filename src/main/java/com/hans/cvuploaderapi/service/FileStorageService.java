package com.hans.cvuploaderapi.service;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
public class FileStorageService {

  private final MinioClient minioClient;
  private final String bucketName;

  public FileStorageService(MinioClient minioClient, @Value("${minio.bucket.name}") String bucketName) {
    this.minioClient = minioClient;
    this.bucketName = bucketName;
  }

  public void uploadFile(MultipartFile file) {
    try {
      checkBucket();

      var objectArgs = PutObjectArgs
              .builder()
              .bucket(bucketName)
              .object(file.getOriginalFilename()).stream(file.getInputStream(), file.getSize(), -1)
              .build();

      minioClient.putObject(objectArgs);
      log.info("Saved file: {} in the bucket {}", file.getOriginalFilename(), bucketName);

    } catch (Exception e) {
      log.error("Happened error when upload file: ", e);
    }
  }

  private void checkBucket() throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
    if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
      minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
      log.info("Bucket was created.");
    }
  }
}
