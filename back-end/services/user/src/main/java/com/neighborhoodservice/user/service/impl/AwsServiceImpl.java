package com.neighborhoodservice.user.service.impl;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.neighborhoodservice.user.service.AwsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
@Primary
public class AwsServiceImpl implements AwsService {

    @Autowired
    private AmazonS3 s3Client;

    // Method to upload a file to an S3 bucket
    @Override
    public void uploadFile(
            final String bucketName,
            final String keyName,
            final Long contentLength,
            final String contentType,
            final InputStream value
    ) throws AmazonClientException {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType);

        s3Client.putObject(bucketName, keyName, value, metadata);
        log.info("File uploaded to bucket({}): {}", bucketName, keyName);
    }

    // Method to download a file from an S3 bucket
    @Override
    public ByteArrayOutputStream downloadFile(
            final String bucketName,
            final String keyName
    ) throws IOException, AmazonClientException {
        S3Object s3Object = s3Client.getObject(bucketName, keyName);
        InputStream inputStream = s3Object.getObjectContent();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int len;
        byte[] buffer = new byte[4096];
        while ((len = inputStream.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, len);
        }

        log.info("File downloaded from bucket({}): {}", bucketName, keyName);
        return outputStream;
    }


    // Method to delete a file from an S3 bucket
    @Override
    public void deleteFile(
            final String bucketName,
            final String keyName
    ) throws AmazonClientException {
        s3Client.deleteObject(bucketName, keyName);
        log.info("File deleted from bucket({}): {}", bucketName, keyName);
    }

    /**
     * Method to check if an object exists in S3 bucket
     *
     * @param keyName The S3 object key (file path)
     * @return boolean indicating whether the object exists in the bucket
     */
    @Override
    public boolean doesObjectExist(String bucketName, String keyName) {
        try {
            // Check if the object exists in the S3 bucket
            s3Client.doesObjectExist(bucketName, keyName);
            log.info("Object with key '{}' exists in the bucket '{}'.", keyName, bucketName);
            return true;
        } catch (Exception e) {
            // If the object does not exist or other errors occur, log it and return false
            log.warn("Object with key '{}' does not exist in the bucket '{}'.", keyName, bucketName);
            return false;
        }
    }

}
