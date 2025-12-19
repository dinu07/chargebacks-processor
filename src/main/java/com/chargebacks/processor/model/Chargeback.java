package com.chargebacks.processor.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Chargeback {
    private LocalDate disputedDt;
    private BigDecimal disputedAmt;
    private String disputedCurr;
    private String merchandiseRef;
    private String reasonForDispute;
    private LocalDateTime createdTime;

    public Chargeback() {
    }

    public Chargeback(LocalDate disputedDt, BigDecimal disputedAmt, String disputedCurr,
                     String merchandiseRef, String reasonForDispute, LocalDateTime createdTime) {
        this.disputedDt = disputedDt;
        this.disputedAmt = disputedAmt;
        this.disputedCurr = disputedCurr;
        this.merchandiseRef = merchandiseRef;
        this.reasonForDispute = reasonForDispute;
        this.createdTime = createdTime;
    }

    public LocalDate getDisputedDt() {
        return disputedDt;
    }

    public void setDisputedDt(LocalDate disputedDt) {
        this.disputedDt = disputedDt;
    }

    public BigDecimal getDisputedAmt() {
        return disputedAmt;
    }

    public void setDisputedAmt(BigDecimal disputedAmt) {
        this.disputedAmt = disputedAmt;
    }

    public String getDisputedCurr() {
        return disputedCurr;
    }

    public void setDisputedCurr(String disputedCurr) {
        this.disputedCurr = disputedCurr;
    }

    public String getMerchandiseRef() {
        return merchandiseRef;
    }

    public void setMerchandiseRef(String merchandiseRef) {
        this.merchandiseRef = merchandiseRef;
    }

    public String getReasonForDispute() {
        return reasonForDispute;
    }

    public void setReasonForDispute(String reasonForDispute) {
        this.reasonForDispute = reasonForDispute;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }
}

