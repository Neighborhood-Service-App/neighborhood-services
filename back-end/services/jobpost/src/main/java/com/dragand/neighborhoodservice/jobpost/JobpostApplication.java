package com.dragand.neighborhoodservice.jobpost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class JobpostApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobpostApplication.class, args);
	}

}
