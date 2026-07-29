package com.finengine.loan_engine.model;

import java.math.BigDecimal;

public class LoanComparison {

    private LoanSummary firstLoan;
    private LoanSummary secondLoan;
    private BigDecimal interestDifference;

    public LoanComparison() {}

    public LoanComparison(LoanSummary firstLoan, LoanSummary secondLoan, BigDecimal interestDifference) {
        this.firstLoan = firstLoan;
        this.secondLoan = secondLoan;
        this.interestDifference = interestDifference;
    }

    public LoanSummary getFirstLoan() { return firstLoan; }
    public void setFirstLoan(LoanSummary firstLoan) { this.firstLoan = firstLoan; }
    public LoanSummary getSecondLoan() { return secondLoan; }
    public void setSecondLoan(LoanSummary secondLoan) { this.secondLoan = secondLoan; }
    public BigDecimal getInterestDifference() { return interestDifference; }
    public void setInterestDifference(BigDecimal interestDifference) { this.interestDifference = interestDifference; }
}