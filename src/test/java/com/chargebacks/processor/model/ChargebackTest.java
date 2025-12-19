package com.chargebacks.processor.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ChargebackTest {

    @Test
    void testDefaultConstructor() {
        Chargeback chargeback = new Chargeback();
        assertNotNull(chargeback);
        assertNull(chargeback.getDisputedDt());
        assertNull(chargeback.getDisputedAmt());
        assertNull(chargeback.getDisputedCurr());
        assertNull(chargeback.getMerchandiseRef());
        assertNull(chargeback.getReasonForDispute());
        assertNull(chargeback.getCreatedTime());
    }

    @Test
    void testParameterizedConstructor() {
        LocalDate disputedDt = LocalDate.of(2024, 1, 15);
        BigDecimal disputedAmt = new BigDecimal("100.50");
        String disputedCurr = "USD";
        String merchandiseRef = "REF-001";
        String reasonForDispute = "Product not received";
        LocalDateTime createdTime = LocalDateTime.of(2024, 1, 15, 10, 0, 0);

        Chargeback chargeback = new Chargeback(disputedDt, disputedAmt, disputedCurr,
                merchandiseRef, reasonForDispute, createdTime);

        assertEquals(disputedDt, chargeback.getDisputedDt());
        assertEquals(disputedAmt, chargeback.getDisputedAmt());
        assertEquals(disputedCurr, chargeback.getDisputedCurr());
        assertEquals(merchandiseRef, chargeback.getMerchandiseRef());
        assertEquals(reasonForDispute, chargeback.getReasonForDispute());
        assertEquals(createdTime, chargeback.getCreatedTime());
    }

    @Test
    void testSettersAndGetters() {
        Chargeback chargeback = new Chargeback();

        LocalDate disputedDt = LocalDate.of(2024, 1, 15);
        BigDecimal disputedAmt = new BigDecimal("250.75");
        String disputedCurr = "EUR";
        String merchandiseRef = "REF-002";
        String reasonForDispute = "Unauthorized transaction";
        LocalDateTime createdTime = LocalDateTime.of(2024, 1, 16, 14, 30, 0);

        chargeback.setDisputedDt(disputedDt);
        chargeback.setDisputedAmt(disputedAmt);
        chargeback.setDisputedCurr(disputedCurr);
        chargeback.setMerchandiseRef(merchandiseRef);
        chargeback.setReasonForDispute(reasonForDispute);
        chargeback.setCreatedTime(createdTime);

        assertEquals(disputedDt, chargeback.getDisputedDt());
        assertEquals(disputedAmt, chargeback.getDisputedAmt());
        assertEquals(disputedCurr, chargeback.getDisputedCurr());
        assertEquals(merchandiseRef, chargeback.getMerchandiseRef());
        assertEquals(reasonForDispute, chargeback.getReasonForDispute());
        assertEquals(createdTime, chargeback.getCreatedTime());
    }
}

