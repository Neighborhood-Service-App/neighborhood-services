ARG CLOUDFRONT_DOMAIN_NAME
ARG CLOUDFRONT_KEY_PAIR_ID
ARG CLOUDFRONT_PRIVATE_KEY
ARG GOOGLE_GEOCODING_API_KEY
ARG KEYCLOAK_ADMIN_CLIENT_ID
ARG KEYCLOAK_ADMIN_PASSWORD
ARG KEYCLOAK_ADMIN_USERNAME
ARG KEYCLOAK_PUBLIC_KEY
ARG KEYCLOAK_REALM
ARG S3_ACCESS_KEY
ARG S3_SECRET_KEY


# Use an official OpenJDK image as the base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from your local machine into the container
COPY target/user-0.0.1-SNAPSHOT.jar /app/user.jar

# Expose the port your service runs on (e.g., 8080)
EXPOSE 8080

# Run the service when the container starts
CMD ["java", "-jar", "gateway.jar"]
