package com.chargebacks.processor.command;

import com.chargebacks.processor.job.ChargebackJobLauncher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargebackCommandTest {

    @Mock
    private ChargebackJobLauncher jobLauncher;

    @InjectMocks
    private ChargebackCommand command;

    @BeforeEach
    void setUp() {
        // Use reflection to inject the mock since @InjectMocks doesn't work with Spring components
        try {
            java.lang.reflect.Field field = ChargebackCommand.class.getDeclaredField("jobLauncher");
            field.setAccessible(true);
            field.set(command, jobLauncher);
        } catch (Exception e) {
            fail("Failed to inject mock: " + e.getMessage());
        }
    }

    @Test
    void testCall_WithProvidedTimestamps() throws Exception {
        // Arrange
        String startTimestamp = "2024-01-01T00:00:00";
        String endTimestamp = "2024-01-31T23:59:59";
        
        try {
            java.lang.reflect.Field startField = ChargebackCommand.class.getDeclaredField("startTimestamp");
            startField.setAccessible(true);
            startField.set(command, startTimestamp);
            
            java.lang.reflect.Field endField = ChargebackCommand.class.getDeclaredField("endTimestamp");
            endField.setAccessible(true);
            endField.set(command, endTimestamp);
        } catch (Exception e) {
            fail("Failed to set fields: " + e.getMessage());
        }

        String expectedOutputFile = "/app/output/chargebacks_20240101_000000_to_20240131_235959.csv";
        when(jobLauncher.launchJob(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedOutputFile);

        // Act
        Integer exitCode = command.call();

        // Assert
        assertEquals(0, exitCode, "Should return success exit code");
        verify(jobLauncher, times(1)).launchJob(
                LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                LocalDateTime.of(2024, 1, 31, 23, 59, 59)
        );
    }

    @Test
    void testCall_WithDefaultTimestamps() throws Exception {
        // Arrange - empty strings should trigger defaults
        try {
            java.lang.reflect.Field startField = ChargebackCommand.class.getDeclaredField("startTimestamp");
            startField.setAccessible(true);
            startField.set(command, "");
            
            java.lang.reflect.Field endField = ChargebackCommand.class.getDeclaredField("endTimestamp");
            endField.setAccessible(true);
            endField.set(command, "");
        } catch (Exception e) {
            fail("Failed to set fields: " + e.getMessage());
        }

        String expectedOutputFile = "/app/output/chargebacks_today.csv";
        when(jobLauncher.launchJob(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedOutputFile);

        // Act
        Integer exitCode = command.call();

        // Assert
        assertEquals(0, exitCode);
        verify(jobLauncher, times(1)).launchJob(any(LocalDateTime.class), any(LocalDateTime.class));
        
        // Verify defaults are used (start of today, end of today)
        verify(jobLauncher).launchJob(
                argThat(start -> start.equals(LocalDate.now().atStartOfDay())),
                argThat(end -> end.equals(LocalDate.now().atTime(23, 59, 59)))
        );
    }

    @Test
    void testCall_WithNullTimestamps() throws Exception {
        // Arrange
        try {
            java.lang.reflect.Field startField = ChargebackCommand.class.getDeclaredField("startTimestamp");
            startField.setAccessible(true);
            startField.set(command, null);
            
            java.lang.reflect.Field endField = ChargebackCommand.class.getDeclaredField("endTimestamp");
            endField.setAccessible(true);
            endField.set(command, null);
        } catch (Exception e) {
            fail("Failed to set fields: " + e.getMessage());
        }

        String expectedOutputFile = "/app/output/chargebacks_today.csv";
        when(jobLauncher.launchJob(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expectedOutputFile);

        // Act
        Integer exitCode = command.call();

        // Assert
        assertEquals(0, exitCode);
        verify(jobLauncher, times(1)).launchJob(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void testCall_WithInvalidTimestampFormat() throws Exception {
        // Arrange
        try {
            java.lang.reflect.Field startField = ChargebackCommand.class.getDeclaredField("startTimestamp");
            startField.setAccessible(true);
            startField.set(command, "invalid-format");
            
            java.lang.reflect.Field endField = ChargebackCommand.class.getDeclaredField("endTimestamp");
            endField.setAccessible(true);
            endField.set(command, "2024-01-31T23:59:59");
        } catch (Exception e) {
            fail("Failed to set fields: " + e.getMessage());
        }

        // Act - command catches exceptions and returns error code
        Integer exitCode = command.call();

        // Assert - should return error exit code (1) for invalid format
        assertEquals(1, exitCode, "Should return error exit code for invalid timestamp format");
    }

    @Test
    void testCall_WhenJobLauncherThrowsException() throws Exception {
        // Arrange
        try {
            java.lang.reflect.Field startField = ChargebackCommand.class.getDeclaredField("startTimestamp");
            startField.setAccessible(true);
            startField.set(command, "2024-01-01T00:00:00");
            
            java.lang.reflect.Field endField = ChargebackCommand.class.getDeclaredField("endTimestamp");
            endField.setAccessible(true);
            endField.set(command, "2024-01-31T23:59:59");
        } catch (Exception e) {
            fail("Failed to set fields: " + e.getMessage());
        }

        when(jobLauncher.launchJob(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        // Act
        Integer exitCode = command.call();

        // Assert
        assertEquals(1, exitCode, "Should return error exit code");
        verify(jobLauncher, times(1)).launchJob(any(LocalDateTime.class), any(LocalDateTime.class));
    }
}

