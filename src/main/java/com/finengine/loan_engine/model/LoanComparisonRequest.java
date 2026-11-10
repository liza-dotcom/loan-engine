package com.finengine.loan_engine.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class LoanComparisonRequest {

    @Valid
    @NotNull(message = "First loan is required")
    private LoanRequest firstLoan;
    @Valid
    @NotNull(message = "Second loan is required")
    private LoanRequest secondLoan;

    public LoanComparisonRequest() {}

    public LoanRequest getFirstLoan() { return firstLoan; }
    public void setFirstLoan(LoanRequest firstLoan) { this.firstLoan = firstLoan; }
    public LoanRequest getSecondLoan() { return secondLoan; }
    public void setSecondLoan(LoanRequest secondLoan) { this.secondLoan = secondLoan; }
}