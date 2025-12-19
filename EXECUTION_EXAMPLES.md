# Execution Examples

This document provides practical examples of how to execute the chargebacks processor.

## Prerequisites

1. Ensure MySQL database is running and accessible
2. Create the `Chargebacks` table with the required schema
3. Have Java 17+ installed
4. Build the application: `mvn clean package`

## Example 1: Extract Chargebacks for a Specific Date Range

```bash
java -jar target/chargebacks-processor-1.0.0.jar \
  --startTimestamp "2024-01-01T00:00:00" \
  --endTimestamp "2024-01-31T23:59:59"
```

**Expected Output:**
- CSV file: `./output/chargebacks_20240101_000000_to_20240131_235959.csv`
- Console: Job completion message with output file path

## Example 2: Extract Chargebacks for Previous Day

```bash
# On Linux/Mac
java -jar target/chargebacks-processor-1.0.0.jar \
  --startTimestamp "$(date -u -d '1 day ago' +%Y-%m-%dT00:00:00)" \
  --endTimestamp "$(date -u -d '1 day ago' +%Y-%m-%dT23:59:59)"
```

## Example 3: Extract Chargebacks for Current Month

```bash
# First day of current month
START=$(date -u +%Y-%m-01T00:00:00)
# Last day of current month
END=$(date -u +%Y-%m-$(date -u +%d)T23:59:59)

java -jar target/chargebacks-processor-1.0.0.jar \
  --startTimestamp "$START" \
  --endTimestamp "$END"
```

## Example 4: Using Docker

```bash
# Build the image
docker build -t chargebacks-processor:1.0.0 .

# Run with environment variables
docker run --rm \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=yourpassword \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/chargebacks_db" \
  -v $(pwd)/output:/app/output \
  chargebacks-processor:1.0.0 \
  --startTimestamp "2024-01-01T00:00:00" \
  --endTimestamp "2024-01-31T23:59:59"
```

## Example 5: Running Integration Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ChargebackProcessorIntegrationTest

# Run with verbose output
mvn test -X
```

## Example 6: Kubernetes CronJob Manual Execution

```bash
# Create a manual job from the CronJob
kubectl create job --from=cronjob/chargeback-processor manual-run-$(date +%s)

# View job status
kubectl get jobs

# View logs
kubectl logs job/manual-run-<timestamp>
```

## Troubleshooting

### Database Connection Issues

If you encounter connection errors, verify:
- Database is accessible from the application
- Credentials are correct (check environment variables or application.yml)
- Database URL includes correct host and port

### No Records Exported

If the CSV file is created but empty (only header row):
- Verify records exist in the database for the specified timestamp range
- Check that `created_time` field is properly indexed for performance
- Verify timestamp format matches: `yyyy-MM-ddTHH:mm:ss`

### Output Directory Issues

If output directory doesn't exist:
- Create it manually: `mkdir -p output`
- Or set `OUTPUT_DIR` environment variable to a writable path

