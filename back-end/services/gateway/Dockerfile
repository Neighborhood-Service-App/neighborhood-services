# Use an official OpenJDK image as the base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from your local machine into the container
COPY target/gateway-0.0.1-SNAPSHOT.jar /app/gateway.jar

# Expose the port your service runs on (e.g., 8080)
EXPOSE 8100

# Run the service when the container starts
CMD ["java", "-jar", "gateway.jar"]
