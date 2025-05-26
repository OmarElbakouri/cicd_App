# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine as build
WORKDIR /workspace/app

# Cache Maven dependencies
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B

# Build the application
COPY src src
RUN ./mvnw package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add labels for better container management
LABEL maintainer="Mini-CI/CD Team"
LABEL version="1.0"
LABEL description="Mini-CI/CD Application"

# Create a non-root user to run the application
RUN addgroup --system --gid 1001 appgroup \
    && adduser --system --uid 1001 --ingroup appgroup appuser

# Set up application
VOLUME /tmp

# Copy the JAR file directly
COPY --from=build /workspace/app/target/*.jar /app/app.jar

# Create workspace directory for builds
RUN mkdir -p /workspace

# Change ownership of the application files
RUN chown -R appuser:appgroup /app /workspace

# Switch to non-root user
USER appuser

# Set environment variables
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Run the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar"]
