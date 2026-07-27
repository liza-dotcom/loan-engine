package com.finengine.loan_engine.model;

import java.math.BigDecimal;

public class RateChange {
    private int effectiveMonth;
    private BigDecimal newAnnualRate;

    public RateChange() {}

    public RateChange(int effectiveMonth, BigDecimal newAnnualRate) {
        this.effectiveMonth = effectiveMonth;
        this.newAnnualRate = newAnnualRate;
    }

    public int getEffectiveMonth() { return effectiveMonth; }
    public void setEffectiveMonth(int effectiveMonth) { this.effectiveMonth = effectiveMonth; }
    public BigDecimal getNewAnnualRate() { return newAnnualRate; }
    public void setNewAnnualRate(BigDecimal newAnnualRate) { this.newAnnualRate = newAnnualRate; }
}