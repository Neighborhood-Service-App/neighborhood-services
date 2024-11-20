package com.neighborhoodservice.user.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String ACCESS_KEY;

    @Value("${cloud.aws.credentials.secretKey}")
    private String SECRET_KEY;

    @Value("${cloud.aws.region.static}")
    private String REGION;

    // Creating a bean for Amazon S3 client
    @Bean
    public AmazonS3 s3Client() {
        // Creating AWS credentials using access key and secret key
        AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);

        // Building Amazon S3 client with specified credentials and region
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(REGION)
                .build();
    }
}
