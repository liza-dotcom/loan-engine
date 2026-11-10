package com.finengine.loan_engine.model;

import jakarta.validation.constraints.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LoanRequest {

    @NotNull(message = "Principal is required")
    @DecimalMin(value = "0.01", message = "Principal must be positive")
    @DecimalMax(value = "100000000", message = "Principal exceeds maximum allowed value")
    private BigDecimal principal;

    @NotNull(message = "Annual interest rate is required")
    @DecimalMin(value = "0.0", message = "Interest rate cannot be negative")
    @DecimalMax(value = "100.0", message = "Interest rate cannot exceed 100%")
    private BigDecimal annualInterestRate;

    @Min(value = 1, message = "Term must be at least 1 month")
    @Max(value = 360, message = "Term cannot exceed 360 months (30 years)")
    private int termMonths;

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    private LocalDate startDate;

    private List<RateChange> rateChangeSchedule;

    @Min(value = 0, message = "Interest-only months cannot be negative")
    private int interestOnlyMonths;

    // class constructor
    public LoanRequest() {
    }
    // create the LoanRequest instance/object

    public LoanRequest(BigDecimal principal, BigDecimal annualInterestRate, int termMonths,
                        LoanType loanType, LocalDate startDate,  List<RateChange> rateChangeSchedule ) {
        this.principal = principal;
        this.annualInterestRate = annualInterestRate;
        this.termMonths = termMonths;
        this.loanType = loanType;
        this.startDate = startDate;
        this.rateChangeSchedule= rateChangeSchedule ; 
    }
    public LoanRequest(BigDecimal principal, BigDecimal annualInterestRate, int termMonths,
                    LoanType loanType, LocalDate startDate, int interestOnlyMonths) {
    this.principal = principal;
    this.annualInterestRate = annualInterestRate;
    this.termMonths = termMonths;
    this.loanType = loanType;
    this.startDate = startDate;
    this.interestOnlyMonths= interestOnlyMonths;
}

    public LoanRequest(BigDecimal principal, BigDecimal annualInterestRate, int termMonths,
                    LoanType loanType, LocalDate startDate ) {
    this.principal = principal;
    this.annualInterestRate = annualInterestRate;
    this.termMonths = termMonths;
    this.loanType = loanType;
    this.startDate = startDate;
    
}

    // declare the menthods/functions for the classes implementing the LoanRequest interface 

    // get the principal amount of loan 
    public BigDecimal getPrincipal() {
        return principal;
    }

    // set the principal amount 
    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    // get the interest rate for loan to use in calculations later

    public BigDecimal getAnnualInterestRate() {
        return annualInterestRate;
    }

    // set the rate on the principal 

    public void setAnnualInterestRate(BigDecimal annualInterestRate) {
        this.annualInterestRate = annualInterestRate;
    }

    // get and set the term (calculated in months) 

    public int getTermMonths() {
        return termMonths;
    }

    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }

    // get and set the type of loan from available types i.e. fixed, variable and interest only
    public LoanType getLoanType() {
        return loanType;
    }

    public void setLoanType(LoanType loanType) {
        this.loanType = loanType;
    }

    // gtw and set the date of start of loan to ge the entire term of duration and calculate rates, interests and total payable 

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // get and set the term object for the variable rate change schedule, the object stores the months with rates for the entire term

    public List<RateChange> getRateChangeSchedule(){
        return rateChangeSchedule;
    }

    public void setRateChangeSchedule(List<RateChange> rateChangeSchedule){
        
        this.rateChangeSchedule= rateChangeSchedule;
    }

    public int getInterestOnlyMonths(){
        return interestOnlyMonths;
    }

    public void setInterestOnlyMonths(int interestOnlyMonths){

        this.interestOnlyMonths= interestOnlyMonths;
    }

    @JsonIgnore
    @AssertTrue(message = "VARIABLE loans need a rateChangeSchedule with at least one rate change")
    public boolean isRateScheduleValidForType() {
        return loanType != LoanType.VARIABLE
            || (rateChangeSchedule != null && !rateChangeSchedule.isEmpty());
    }

}