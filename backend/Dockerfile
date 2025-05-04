# Use OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory in the container
WORKDIR /app

# Copy the JAR file from the target directory
COPY target/ecommerce-shop-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8088

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
