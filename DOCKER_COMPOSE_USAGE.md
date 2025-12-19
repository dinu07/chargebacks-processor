# Docker Compose Usage Guide

This guide demonstrates how to use the chargebacks-processor application with Docker Compose, including a MySQL database with pre-populated mock data.

## Prerequisites

- Docker and Docker Compose installed
- Port 3306 available (or modify the port mapping in docker-compose.yml)

## Quick Start

### 1. Start the Services

Start MySQL and build the application:

```bash
docker-compose up -d mysql-service
```

Wait for MySQL to be ready (health check will verify this), then build and start the application:

```bash
docker-compose build chargebacks-processor
docker-compose up -d
```

Or start everything at once:

```bash
docker-compose up -d
```

### 2. Verify MySQL is Running

Check the MySQL container logs:

```bash
docker-compose logs mysql-service
```

You should see messages indicating the database is initialized with the schema and mock data.

### 3. Run the Application

#### Example 1: Export Today's Chargebacks (Using Defaults)

Since the command-line options now default to today's date range, you can run:

```bash
docker-compose run --rm chargebacks-processor
```

This will export all chargebacks created today (from 00:00:00 to 23:59:59).

#### Example 2: Export Chargebacks for a Specific Date Range

```bash
docker-compose run --rm chargebacks-processor \
  --startTimestamp "2024-12-15T00:00:00" \
  --endTimestamp "2024-12-19T23:59:59"
```

#### Example 3: Export Yesterday's Chargebacks

```bash
# Get yesterday's date
YESTERDAY=$(date -u -d '1 day ago' +%Y-%m-%d)

docker-compose run --rm chargebacks-processor \
  --startTimestamp "${YESTERDAY}T00:00:00" \
  --endTimestamp "${YESTERDAY}T23:59:59"
```

#### Example 4: Export Last 7 Days

```bash
START_DATE=$(date -u -d '7 days ago' +%Y-%m-%d)
END_DATE=$(date -u +%Y-%m-%d)

docker-compose run --rm chargebacks-processor \
  --startTimestamp "${START_DATE}T00:00:00" \
  --endTimestamp "${END_DATE}T23:59:59"
```

### 4. View Output Files

The CSV output files are written to the `./output` directory on your host machine:

```bash
ls -lh output/
cat output/chargebacks_*.csv
```

### 5. Check Application Logs

View the application logs:

```bash
docker-compose logs chargebacks-processor
```

### 6. Connect to MySQL (Optional)

You can connect to the MySQL database to verify the data:

```bash
docker-compose exec mysql-service mysql -uroot -prootpassword chargebacks_db
```

Then run SQL queries:

```sql
-- Count total records
SELECT COUNT(*) FROM Chargebacks;

-- View recent records
SELECT * FROM Chargebacks ORDER BY created_time DESC LIMIT 10;

-- View records by date range
SELECT * FROM Chargebacks 
WHERE created_time >= DATE_SUB(NOW(), INTERVAL 2 DAY)
  AND created_time <= NOW()
ORDER BY created_time;
```

Exit MySQL: `exit`

## Mock Data Overview

The mock data includes:
- **14 chargeback records** with various dates relative to today
- Records from 5 days ago to 2 days in the future
- Different currencies: USD, EUR, GBP
- Various dispute reasons
- Different amounts ranging from $75 to $500

## Stopping Services

Stop all services:

```bash
docker-compose down
```

Stop and remove volumes (this will delete all data):

```bash
docker-compose down -v
```

## Troubleshooting

### MySQL Connection Issues

If you see connection errors, wait a bit longer for MySQL to fully initialize:

```bash
docker-compose logs mysql-service | tail -20
```

### Port Already in Use

If port 3306 is already in use, modify the port mapping in `docker-compose.yml`:

```yaml
ports:
  - "3307:3306"  # Change 3306 to 3307 or another available port
```

### Application Can't Find Output Directory

The output directory is automatically created. If you see permission errors:

```bash
mkdir -p output
chmod 777 output
```

### View All Logs

```bash
docker-compose logs -f
```

## Example Workflow

Complete example workflow:

```bash
# 1. Start services
docker-compose up -d

# 2. Wait for MySQL to be ready (check logs)
docker-compose logs mysql-service | grep "ready for connections"

# 3. Export today's chargebacks
docker-compose run --rm chargebacks-processor

# 4. View the output
ls -lh output/
cat output/chargebacks_*.csv

# 5. Export last 3 days
docker-compose run --rm chargebacks-processor \
  --startTimestamp "$(date -u -d '3 days ago' +%Y-%m-%d)T00:00:00" \
  --endTimestamp "$(date -u +%Y-%m-%d)T23:59:59"

# 6. Clean up
docker-compose down
```

## Database Schema

The MySQL database includes:
- Database name: `chargebacks_db`
- Table: `Chargebacks`
- Root user: `root` / `rootpassword`
- Application user: `chargeback_user` / `chargeback_pass`

## Network

All services run on the `chargebacks-network` bridge network, allowing them to communicate using service names (e.g., `mysql-service`).

