package com.finengine.loan_engine.model;

import java.math.BigDecimal;

/*
This class represents a summary of a calculated loan schedule, including
total payments, total interest paid, total principal paid, and the effective interest rate.
*/
public class LoanSummary {

    // total amount paid by the borrower through the loan term
    private BigDecimal totalPayment; 
    //total interset paid over the term
    private BigDecimal totalInterest;
    // total principal amount paid (i.e. totalPAyment-total interest) the amount that counted towards reduceing the borrowed amount
    private BigDecimal totalPrincipal;
    // the rate of interest thats effective on the loan based on the terms and conditions of loan 
    private BigDecimal effectiveRate;
    public LoanSummary() {
    }

    public LoanSummary(BigDecimal totalPayment, BigDecimal totalInterest,
                        BigDecimal totalPrincipal, BigDecimal effectiveRate) {
        this.totalPayment = totalPayment;
        this.totalInterest = totalInterest;
        this.totalPrincipal = totalPrincipal;
        this.effectiveRate = effectiveRate;
    }

    public BigDecimal getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(BigDecimal totalPayment) {
        this.totalPayment = totalPayment;
    }

    public BigDecimal getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(BigDecimal totalInterest) {
        this.totalInterest = totalInterest;
    }

    public BigDecimal getTotalPrincipal() {
        return totalPrincipal;
    }

    public void setTotalPrincipal(BigDecimal totalPrincipal) {
        this.totalPrincipal = totalPrincipal;
    }

    public BigDecimal getEffectiveRate() {
        return effectiveRate;
    }

    public void setEffectiveRate(BigDecimal effectiveRate) {
        this.effectiveRate = effectiveRate;
    }
}