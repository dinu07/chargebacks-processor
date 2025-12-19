# Unit Test Coverage

This document describes the unit test coverage for the chargebacks-processor application.

## Test Structure

The project includes comprehensive unit tests for the main components:

### Test Classes

1. **ChargebackTest** (`src/test/java/com/chargebacks/processor/model/ChargebackTest.java`)
   - Tests the Chargeback model class
   - Covers default constructor, parameterized constructor, and all getters/setters
   - **Coverage**: Model class (100%)

2. **ChargebackItemProcessorTest** (`src/test/java/com/chargebacks/processor/processor/ChargebackItemProcessorTest.java`)
   - Tests the pass-through processor
   - Verifies that items are returned unchanged
   - Tests with null values and different currencies
   - **Coverage**: Processor class (100%)

3. **ChargebackItemWriterTest** (`src/test/java/com/chargebacks/processor/writer/ChargebackItemWriterTest.java`)
   - Tests CSV file writing functionality
   - Verifies output directory creation
   - Tests file naming with timestamps
   - Tests field extraction and formatting
   - Tests null value handling
   - **Coverage**: Writer class (high coverage)

4. **ChargebackCommandTest** (`src/test/java/com/chargebacks/processor/command/ChargebackCommandTest.java`)
   - Tests command-line argument parsing
   - Tests default timestamp behavior
   - Tests error handling for invalid formats
   - Tests job launcher integration
   - **Coverage**: Command class (high coverage)

5. **ChargebackProcessorIntegrationTest** (`src/test/java/com/chargebacks/processor/ChargebackProcessorIntegrationTest.java`)
   - Integration tests with H2 database
   - Tests end-to-end ETL process
   - Tests CSV format validation
   - **Coverage**: Integration scenarios

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Only Unit Tests (Excluding Integration Tests)
```bash
mvn test -Dtest="*Test" -Dtest="!*IntegrationTest"
```

### Run Specific Test Class
```bash
mvn test -Dtest=ChargebackTest
mvn test -Dtest=ChargebackItemProcessorTest
mvn test -Dtest=ChargebackItemWriterTest
mvn test -Dtest=ChargebackCommandTest
```

### Generate Coverage Report
```bash
mvn test jacoco:report
```

The coverage report will be available at:
```
target/site/jacoco/index.html
```

## Coverage Goals

The project is configured with JaCoCo to enforce a minimum coverage of **70%** for line coverage.

### Current Coverage Areas

- ✅ **Model Classes**: 100% coverage
- ✅ **Processor Classes**: 100% coverage  
- ✅ **Writer Classes**: High coverage (file operations, formatting)
- ✅ **Command Classes**: High coverage (argument parsing, defaults)
- ⚠️ **Reader Classes**: Covered via integration tests (database-dependent)
- ⚠️ **Job Launcher**: Covered via integration tests (Spring Batch-dependent)

## Test Dependencies

- **JUnit 5**: Test framework
- **Mockito**: Mocking framework for unit tests
- **Spring Boot Test**: Spring testing support
- **Spring Batch Test**: Batch testing utilities
- **H2 Database**: In-memory database for integration tests
- **JaCoCo**: Code coverage tool

## Best Practices

1. **Unit Tests**: Test individual components in isolation
2. **Integration Tests**: Test complete workflows with real dependencies
3. **Mock External Dependencies**: Use Mockito for external services
4. **Test Edge Cases**: Include null values, empty strings, boundary conditions
5. **Maintain Coverage**: Aim for at least 70% line coverage

## Viewing Coverage Reports

After running tests with coverage:

1. Open `target/site/jacoco/index.html` in a web browser
2. Navigate through packages to see detailed coverage
3. Click on classes to see line-by-line coverage
4. Red lines indicate uncovered code
5. Yellow lines indicate partially covered branches

## Continuous Integration

The JaCoCo plugin is configured to fail the build if coverage drops below 70%. To adjust this threshold, modify the `jacoco-maven-plugin` configuration in `pom.xml`.

