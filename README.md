# Chargebacks Processor

ETL processor to extract chargeback records from a MySQL database table based on creation timestamp and export them to CSV format.

## Features

- **Spring Batch**: Batch processing framework for efficient data extraction
- **Picocli**: Command-line interface for easy execution
- **MySQL Integration**: Direct database connectivity
- **CSV Export**: Structured CSV output with proper formatting
- **Integration Tests**: Comprehensive test suite with examples
- **Docker Support**: Containerized application
- **Kubernetes CronJob**: Scheduled deployment manifest

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- MySQL 8.0+
- Docker (optional, for containerization)
- Kubernetes cluster (optional, for CronJob deployment)

## Database Schema

The `Chargebacks` table should have the following structure:

```sql
CREATE TABLE Chargebacks (
    disputed_dt DATE,
    disputed_amt DECIMAL(19, 2),
    disputed_curr VARCHAR(3),
    merchandise_ref VARCHAR(255),
    reason_for_dispute VARCHAR(500),
    created_time TIMESTAMP
);
```

## Configuration

Configure the database connection in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/chargebacks_db
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
```

Or set environment variables:
- `DB_USERNAME`: MySQL username
- `DB_PASSWORD`: MySQL password
- `OUTPUT_DIR`: Output directory for CSV files (default: ./output)

## Building the Application

```bash
mvn clean package
```

## Running the Application

### Command-Line Usage

```bash
java -jar target/chargebacks-processor-1.0.0.jar \
  --startTimestamp "2024-01-01T00:00:00" \
  --endTimestamp "2024-01-31T23:59:59"
```

### Example Execution

```bash
# Extract chargebacks for January 2024
java -jar target/chargebacks-processor-1.0.0.jar \
  --startTimestamp "2024-01-01T00:00:00" \
  --endTimestamp "2024-01-31T23:59:59"

# Output will be saved to: ./output/chargebacks_20240101_000000_to_20240131_235959.csv
```

## Running Integration Tests

```bash
mvn test
```

The integration tests include:
- Test with records within timestamp range
- Test with no matching records
- CSV format validation

## Docker Usage

### Option 1: Docker Compose (Recommended for Demo)

The easiest way to run the application with a MySQL database and mock data:

```bash
# Start MySQL with mock data
docker-compose up -d mysql-service

# Build and run the application (with defaults - today's date)
docker-compose build chargebacks-processor
docker-compose run --rm chargebacks-processor

# Or run with custom date range
docker-compose run --rm chargebacks-processor \
  --startTimestamp "2024-01-01T00:00:00" \
  --endTimestamp "2024-01-31T23:59:59"

# Run the complete demo script
./demo.sh

# Stop services
docker-compose down
```

See [DOCKER_COMPOSE_USAGE.md](DOCKER_COMPOSE_USAGE.md) for detailed usage instructions.

### Option 2: Standalone Docker

#### Build Docker Image

```bash
docker build -t chargebacks-processor:1.0.0 .
```

#### Run Docker Container

```bash
docker run --rm \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/chargebacks_db" \
  -v $(pwd)/output:/app/output \
  chargebacks-processor:1.0.0 \
  --startTimestamp "2024-01-01T00:00:00" \
  --endTimestamp "2024-01-31T23:59:59"
```

## Kubernetes Deployment

### Deploy CronJob

```bash
kubectl apply -f k8s/cronjob.yaml
```

The CronJob is configured to run daily at 2 AM UTC, processing chargebacks from the previous day.

### Manual Job Execution

```bash
kubectl create job --from=cronjob/chargeback-processor manual-run-$(date +%s)
```

### View Logs

```bash
kubectl logs -l job-name=chargeback-processor-<timestamp>
```

## Output Format

The CSV output includes the following columns:
- `disputed_dt`: Date of dispute
- `disputed_amt`: Disputed amount
- `disputed_curr`: Currency code
- `merchandise_ref`: Merchandise reference identifier
- `reason_for_dispute`: Dispute reason description
- `created_time`: Record creation timestamp

## License

This project is licensed under the MIT License.

