#!/bin/bash
# Demo script for chargebacks-processor with Docker Compose

set -e

echo "========================================="
echo "Chargebacks Processor Docker Compose Demo"
echo "========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}Step 1: Starting MySQL service...${NC}"
docker-compose up -d mysql-service

echo -e "${BLUE}Waiting for MySQL to be ready...${NC}"
sleep 10

# Wait for MySQL to be healthy
MAX_WAIT=60
WAIT_COUNT=0
until docker-compose exec -T mysql-service mysqladmin ping -h localhost -uroot -prootpassword --silent 2>/dev/null; do
    if [ $WAIT_COUNT -ge $MAX_WAIT ]; then
        echo -e "${YELLOW}MySQL health check timeout. Continuing anyway...${NC}"
        break
    fi
    echo "Waiting for MySQL... ($WAIT_COUNT/$MAX_WAIT)"
    sleep 2
    WAIT_COUNT=$((WAIT_COUNT + 2))
done

echo -e "${GREEN}MySQL is ready!${NC}"
echo ""

echo -e "${BLUE}Step 2: Building the application...${NC}"
docker-compose build chargebacks-processor

echo ""
echo -e "${BLUE}Step 3: Verifying mock data in MySQL...${NC}"
RECORD_COUNT=$(docker-compose exec -T mysql-service mysql -uroot -prootpassword chargebacks_db -e "SELECT COUNT(*) as count FROM Chargebacks;" -s -N 2>/dev/null || echo "0")
if [ -z "$RECORD_COUNT" ] || [ "$RECORD_COUNT" = "0" ]; then
    echo -e "${YELLOW}Warning: Could not retrieve record count or database is empty${NC}"
    echo "This might be normal if MySQL is still initializing. Continuing..."
else
    echo -e "${GREEN}Found $RECORD_COUNT chargeback records in the database${NC}"
fi
echo ""

echo -e "${BLUE}Step 4: Running the application with default settings (today's date)...${NC}"
docker-compose run --rm chargebacks-processor

echo ""
echo -e "${BLUE}Step 5: Listing output files...${NC}"
ls -lh output/ | grep -E "\.csv$" || echo "No CSV files found"

echo ""
echo -e "${BLUE}Step 6: Displaying the latest CSV file...${NC}"
LATEST_CSV=$(ls -t output/*.csv 2>/dev/null | head -1)
if [ -n "$LATEST_CSV" ]; then
    echo -e "${GREEN}Latest file: $LATEST_CSV${NC}"
    echo ""
    echo "First 10 lines:"
    head -10 "$LATEST_CSV"
else
    echo -e "${YELLOW}No CSV files found${NC}"
fi

echo ""
echo -e "${BLUE}Step 7: Running with custom date range (last 3 days)...${NC}"
# Use date command compatible with both Linux (GNU) and macOS (BSD)
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS date command
    START_DATE=$(date -u -v-3d +%Y-%m-%d)
    END_DATE=$(date -u +%Y-%m-%d)
else
    # Linux date command
    START_DATE=$(date -u -d '3 days ago' +%Y-%m-%d)
    END_DATE=$(date -u +%Y-%m-%d)
fi
echo "Date range: $START_DATE to $END_DATE"

docker-compose run --rm chargebacks-processor \
  --startTimestamp "${START_DATE}T00:00:00" \
  --endTimestamp "${END_DATE}T23:59:59"

echo ""
echo -e "${GREEN}Demo completed!${NC}"
echo ""
echo "To view all output files: ls -lh output/"
echo "To stop services: docker-compose down"
echo "To view logs: docker-compose logs"

