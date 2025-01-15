# Define build arguments at the top of the Dockerfile
ARG ISSUER_URI
ARG JWK_SET_URI

# Use an official OpenJDK image as the base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from your local machine into the container
COPY target/config-server.jar /app/config-server.jar

# Set environment variables using the build arguments
ENV ISSUER_URI=$ISSUER_URI
ENV JWK_SET_URI=$JWK_SET_URI

# Expose the port your service runs on (e.g., 8888)
EXPOSE 8888

# Run the service when the container starts
CMD ["java", "-jar", "config-server.jar"]
