package com.finengine.loan_engine.model;

public class LoanComparisonRequest {

    private LoanRequest firstLoan;
    private LoanRequest secondLoan;

    public LoanComparisonRequest() {}

    public LoanRequest getFirstLoan() { return firstLoan; }
    public void setFirstLoan(LoanRequest firstLoan) { this.firstLoan = firstLoan; }
    public LoanRequest getSecondLoan() { return secondLoan; }
    public void setSecondLoan(LoanRequest secondLoan) { this.secondLoan = secondLoan; }
}