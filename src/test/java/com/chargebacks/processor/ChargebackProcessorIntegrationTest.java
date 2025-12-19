package com.chargebacks.processor;

import com.chargebacks.processor.job.ChargebackJobLauncher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Disabled
class ChargebackProcessorIntegrationTest {

    @Autowired
    private ChargebackJobLauncher jobLauncher;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() throws IOException {
        // Clear existing data
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "Chargebacks");

        // Load test data
        String schema = new String(new ClassPathResource("schema.sql").getInputStream().readAllBytes());
        jdbcTemplate.execute(schema);

        // Insert test data
        insertTestData();
    }

    private void insertTestData() {
        // Use today's date as the base, with time set to 10:00:00
        LocalDateTime baseTime = LocalDate.now().atTime(10, 0, 0);

        // Insert records within range
        insertChargeback(
            LocalDate.now().minusDays(5),
            new BigDecimal("100.50"),
            "USD",
            "REF-001",
            "Product not received",
            baseTime.minusDays(2)
        );

        insertChargeback(
            LocalDate.now().minusDays(3),
            new BigDecimal("250.75"),
            "EUR",
            "REF-002",
            "Unauthorized transaction",
            baseTime.minusDays(1)
        );

        insertChargeback(
            LocalDate.now().minusDays(1),
            new BigDecimal("75.25"),
            "GBP",
            "REF-003",
            "Duplicate charge",
            baseTime
        );

        // Insert record outside range (should not be exported)
        insertChargeback(
            LocalDate.now().plusDays(1),
            new BigDecimal("500.00"),
            "USD",
            "REF-004",
            "Fraudulent transaction",
            baseTime.plusDays(3)
        );
    }

    private void insertChargeback(LocalDate disputedDt, BigDecimal disputedAmt, 
                                  String disputedCurr, String merchandiseRef,
                                  String reasonForDispute, LocalDateTime createdTime) {
        String sql = "INSERT INTO Chargebacks (disputed_dt, disputed_amt, disputed_curr, " +
                    "merchandise_ref, reason_for_dispute, created_time) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, disputedDt, disputedAmt, disputedCurr, 
                           merchandiseRef, reasonForDispute, createdTime);
    }

    @Test
    void testChargebackExportJob() throws Exception {
        // Arrange - Use today's date as base, test range from 2 days ago to end of yesterday
        // This should capture records created 2 days ago and 1 day ago (2 records total)
        LocalDateTime startTimestamp = LocalDate.now().minusDays(2).atStartOfDay();
        LocalDateTime endTimestamp = LocalDate.now().minusDays(1).atTime(23, 59, 59);

        // Act
        String outputFile = jobLauncher.launchJob(startTimestamp, endTimestamp);

        // Assert
        assertNotNull(outputFile);
        assertTrue(Files.exists(Paths.get(outputFile)), "Output file should exist");

        // Verify CSV content
        List<String> lines = Files.readAllLines(Paths.get(outputFile));
        assertTrue(lines.size() >= 2, "Should have header and at least one data row");
        assertEquals("disputed_dt,disputed_amt,disputed_curr,merchandise_ref,reason_for_dispute,created_time", 
                    lines.get(0), "Header should match");

        // Verify that only records within range are exported
        int dataRows = lines.size() - 1; // Exclude header
        assertEquals(2, dataRows, "Should export 2 records within the timestamp range");
    }

    @Test
    void testChargebackExportJobWithNoRecords() throws Exception {
        // Arrange - Use future dates (1 year from now) to ensure no records match
        LocalDateTime startTimestamp = LocalDate.now().plusYears(1).atStartOfDay();
        LocalDateTime endTimestamp = LocalDate.now().plusYears(1).atTime(23, 59, 59);

        // Act
        String outputFile = jobLauncher.launchJob(startTimestamp, endTimestamp);

        // Assert
        assertNotNull(outputFile);
        assertTrue(Files.exists(Paths.get(outputFile)), "Output file should exist");

        // Verify CSV has only header
        List<String> lines = Files.readAllLines(Paths.get(outputFile));
        assertEquals(1, lines.size(), "Should have only header row");
        assertEquals("disputed_dt,disputed_amt,disputed_curr,merchandise_ref,reason_for_dispute,created_time", 
                    lines.get(0));
    }

    @Test
    void testCsvFormat() throws Exception {
        // Arrange - Use a wide range from 30 days ago to tomorrow to capture all test records
        LocalDateTime startTimestamp = LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime endTimestamp = LocalDate.now().plusDays(1).atTime(23, 59, 59);

        // Act
        String outputFile = jobLauncher.launchJob(startTimestamp, endTimestamp);

        // Assert
        try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
            String header = reader.readLine();
            assertNotNull(header);
            assertEquals(6, header.split(",").length, "Header should have 6 columns");

            String line;
            int rowCount = 0;
            while ((line = reader.readLine()) != null) {
                rowCount++;
                String[] fields = line.split(",");
                assertEquals(6, fields.length, "Each data row should have 6 columns");
            }
            assertTrue(rowCount >= 3, "Should have at least 3 data rows");
        }
    }
}

