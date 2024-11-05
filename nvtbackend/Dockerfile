# Use a base image with Java 17 (or whatever version you are using)
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper files and pom.xml separately for caching purposes
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Install dependencies without building the application
RUN ./mvnw dependency:go-offline

# Copy the source code into the container
COPY src ./src

# Package the application
RUN ./mvnw clean package -DskipTests

# Expose the port that your Spring Boot app runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/nvt-0.0.1-SNAPSHOT.jar"]
