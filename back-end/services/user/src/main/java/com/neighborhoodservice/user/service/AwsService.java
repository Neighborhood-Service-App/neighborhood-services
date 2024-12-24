package com.neighborhoodservice.user.service;

import com.amazonaws.AmazonClientException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface AwsService {

    // Method to upload a file to an S3 bucket
    void uploadFile(
            String bucketName,
            String keyName,
            Long contentLength,
            String contentType,
            InputStream value
    ) throws AmazonClientException;

    // Method to download a file from an S3 bucket
    ByteArrayOutputStream downloadFile(
            String bucketName,
            String keyName
    ) throws IOException, AmazonClientException;

    // Method to delete a file from an S3 bucket
    void deleteFile(
            String bucketName,
            String keyName
    ) throws AmazonClientException;

    boolean doesObjectExist(String bucketName, String keyName);
}
