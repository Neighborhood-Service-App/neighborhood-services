# Use an official OpenJDK image as the base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from your local machine into the container
COPY target/discovery.jar /app/discovery.jar

# Expose the port your service runs on (e.g., 8080)
EXPOSE 8761

# Run the service when the container starts
CMD ["java", "-jar", "discovery.jar"]
