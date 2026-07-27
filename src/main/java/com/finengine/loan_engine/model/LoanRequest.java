package com.finengine.loan_engine.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class LoanRequest {

    private BigDecimal principal;
    private BigDecimal annualInterestRate;
    private int termMonths;
    private LoanType loanType;
    private LocalDate startDate;
    private List<RateChange> rateChangeSchedule;
// + getter/setter, + import java.util.List

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
                    LoanType loanType, LocalDate startDate) {
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

}