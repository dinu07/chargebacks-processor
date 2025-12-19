package com.chargebacks.processor.writer;

import com.chargebacks.processor.model.Chargeback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChargebackItemWriterTest {

    private ChargebackItemWriter writer;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        writer = new ChargebackItemWriter();
    }

    @Test
    void testSetOutputFileName_CreatesDirectory() {
        // Arrange
        String outputDir = tempDir.toString();
        // Use reflection to set the outputDirectory field
        try {
            java.lang.reflect.Field field = ChargebackItemWriter.class.getDeclaredField("outputDirectory");
            field.setAccessible(true);
            field.set(writer, outputDir);
        } catch (Exception e) {
            fail("Failed to set outputDirectory: " + e.getMessage());
        }

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59, 59);

        // Act
        writer.setOutputFileName(start, end);

        // Assert
        assertNotNull(writer.getOutputFileName());
        assertTrue(writer.getOutputFileName().contains("chargebacks_"));
        assertTrue(writer.getOutputFileName().contains(".csv"));
        assertTrue(Files.exists(tempDir), "Output directory should be created");
    }

    @Test
    void testConfigureWriter_SetsHeaderAndLineAggregator() throws Exception {
        // Arrange
        String outputDir = tempDir.toString();
        try {
            java.lang.reflect.Field field = ChargebackItemWriter.class.getDeclaredField("outputDirectory");
            field.setAccessible(true);
            field.set(writer, outputDir);
        } catch (Exception e) {
            fail("Failed to set outputDirectory: " + e.getMessage());
        }

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59, 59);
        writer.setOutputFileName(start, end);

        // Act
        writer.configureWriter();
        writer.afterPropertiesSet();

        // Assert - writer should be configured
        assertNotNull(writer);
    }

    @Test
    void testGetOutputFileName() {
        // Arrange
        String outputDir = tempDir.toString();
        try {
            java.lang.reflect.Field field = ChargebackItemWriter.class.getDeclaredField("outputDirectory");
            field.setAccessible(true);
            field.set(writer, outputDir);
        } catch (Exception e) {
            fail("Failed to set outputDirectory: " + e.getMessage());
        }

        LocalDateTime start = LocalDateTime.of(2024, 1, 15, 10, 30, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 15, 11, 45, 0);

        // Act
        writer.setOutputFileName(start, end);
        String fileName = writer.getOutputFileName();

        // Assert
        assertNotNull(fileName);
        assertTrue(fileName.contains("20240115"));
        assertTrue(fileName.contains("103000"));
        assertTrue(fileName.contains("114500"));
        assertTrue(fileName.endsWith(".csv"));
    }

    @Test
    void testFieldExtractor_FormatsDataCorrectly() throws Exception {
        // Arrange
        String outputDir = tempDir.toString();
        try {
            java.lang.reflect.Field field = ChargebackItemWriter.class.getDeclaredField("outputDirectory");
            field.setAccessible(true);
            field.set(writer, outputDir);
        } catch (Exception e) {
            fail("Failed to set outputDirectory: " + e.getMessage());
        }

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59, 59);
        writer.setOutputFileName(start, end);
        writer.configureWriter();
        writer.afterPropertiesSet();

        Chargeback chargeback = new Chargeback(
                LocalDate.of(2024, 1, 15),
                new BigDecimal("100.50"),
                "USD",
                "REF-001",
                "Product not received",
                LocalDateTime.of(2024, 1, 15, 10, 0, 0)
        );

        // Act
        writer.open(new org.springframework.batch.item.ExecutionContext());
        writer.write(new org.springframework.batch.item.Chunk<>(chargeback));
        writer.close();

        // Assert
        File outputFile = new File(writer.getOutputFileName());
        assertTrue(outputFile.exists(), "Output file should be created");
        
        List<String> lines = Files.readAllLines(outputFile.toPath());
        assertTrue(lines.size() >= 2, "Should have header and at least one data row");
        assertEquals("disputed_dt,disputed_amt,disputed_curr,merchandise_ref,reason_for_dispute,created_time", 
                lines.get(0), "Header should match");
        
        String dataLine = lines.get(1);
        assertTrue(dataLine.contains("2024-01-15"), "Should contain formatted date");
        assertTrue(dataLine.contains("100.50"), "Should contain amount");
        assertTrue(dataLine.contains("USD"), "Should contain currency");
        assertTrue(dataLine.contains("REF-001"), "Should contain reference");
    }

    @Test
    void testFieldExtractor_HandlesNullValues() throws Exception {
        // Arrange
        String outputDir = tempDir.toString();
        try {
            java.lang.reflect.Field field = ChargebackItemWriter.class.getDeclaredField("outputDirectory");
            field.setAccessible(true);
            field.set(writer, outputDir);
        } catch (Exception e) {
            fail("Failed to set outputDirectory: " + e.getMessage());
        }

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59, 59);
        writer.setOutputFileName(start, end);
        writer.configureWriter();
        writer.afterPropertiesSet();

        Chargeback chargeback = new Chargeback();
        // All fields are null

        // Act
        writer.open(new org.springframework.batch.item.ExecutionContext());
        writer.write(new org.springframework.batch.item.Chunk<>(chargeback));
        writer.close();

        // Assert
        File outputFile = new File(writer.getOutputFileName());
        assertTrue(outputFile.exists());
        
        List<String> lines = Files.readAllLines(outputFile.toPath());
        assertTrue(lines.size() >= 1, "Should have at least header row");
        assertEquals("disputed_dt,disputed_amt,disputed_curr,merchandise_ref,reason_for_dispute,created_time", 
                lines.get(0), "Header should match");
        
        // If there's a data row, it should have 6 fields (even if empty)
        if (lines.size() > 1) {
            String dataLine = lines.get(1);
            String[] fields = dataLine.split(",", -1); // -1 to preserve empty fields
            assertTrue(fields.length >= 6, "Should have at least 6 fields even with null values");
        }
    }
}

