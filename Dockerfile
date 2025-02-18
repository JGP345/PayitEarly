# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and project files
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make sure mvnw is executable
RUN chmod +x mvnw

# Run the build process
RUN ./mvnw dependency:resolve
COPY src/ src/
RUN ./mvnw clean package

# Use a smaller base image for the final runtime
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/pay-it-early-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]
