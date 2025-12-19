FROM liferay/jdk21:latest


WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create output directory
RUN mkdir -p /app/output

# Copy the JAR from build directory to app directory
RUN cp target/chargebacks-processor-1.0.0.jar app.jar

# Set environment variables
ENV DB_USERNAME=root
ENV DB_PASSWORD=password
ENV OUTPUT_DIR=/app/output

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

