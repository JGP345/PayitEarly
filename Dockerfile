# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy and build the application inside the container
COPY . .

# Build the application inside the container
RUN ./mvnw clean package

# Copy the built JAR file
COPY target/pay-it-early-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]
