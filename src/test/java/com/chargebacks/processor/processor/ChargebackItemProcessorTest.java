package com.chargebacks.processor.processor;

import com.chargebacks.processor.model.Chargeback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ChargebackItemProcessorTest {

    private ChargebackItemProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ChargebackItemProcessor();
    }

    @Test
    void testProcess_ReturnsSameChargeback() throws Exception {
        // Arrange
        Chargeback chargeback = new Chargeback(
                LocalDate.of(2024, 1, 15),
                new BigDecimal("100.50"),
                "USD",
                "REF-001",
                "Product not received",
                LocalDateTime.of(2024, 1, 15, 10, 0, 0)
        );

        // Act
        Chargeback result = processor.process(chargeback);

        // Assert
        assertNotNull(result);
        assertSame(chargeback, result, "Processor should return the same instance (pass-through)");
        assertEquals(chargeback.getDisputedDt(), result.getDisputedDt());
        assertEquals(chargeback.getDisputedAmt(), result.getDisputedAmt());
        assertEquals(chargeback.getDisputedCurr(), result.getDisputedCurr());
        assertEquals(chargeback.getMerchandiseRef(), result.getMerchandiseRef());
        assertEquals(chargeback.getReasonForDispute(), result.getReasonForDispute());
        assertEquals(chargeback.getCreatedTime(), result.getCreatedTime());
    }

    @Test
    void testProcess_WithNullValues() throws Exception {
        // Arrange
        Chargeback chargeback = new Chargeback();
        chargeback.setDisputedDt(null);
        chargeback.setDisputedAmt(null);
        chargeback.setDisputedCurr(null);
        chargeback.setMerchandiseRef(null);
        chargeback.setReasonForDispute(null);
        chargeback.setCreatedTime(null);

        // Act
        Chargeback result = processor.process(chargeback);

        // Assert
        assertNotNull(result);
        assertSame(chargeback, result);
        assertNull(result.getDisputedDt());
        assertNull(result.getDisputedAmt());
    }

    @Test
    void testProcess_WithDifferentCurrencies() throws Exception {
        // Arrange
        Chargeback usdChargeback = new Chargeback(
                LocalDate.of(2024, 1, 15),
                new BigDecimal("100.00"),
                "USD",
                "REF-USD",
                "USD transaction",
                LocalDateTime.now()
        );

        Chargeback eurChargeback = new Chargeback(
                LocalDate.of(2024, 1, 15),
                new BigDecimal("85.50"),
                "EUR",
                "REF-EUR",
                "EUR transaction",
                LocalDateTime.now()
        );

        // Act
        Chargeback usdResult = processor.process(usdChargeback);
        Chargeback eurResult = processor.process(eurChargeback);

        // Assert
        assertEquals("USD", usdResult.getDisputedCurr());
        assertEquals("EUR", eurResult.getDisputedCurr());
    }
}

