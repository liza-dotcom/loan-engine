package com.finengine.loan_engine.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentPeriod {

    private int periodNumber;
    private LocalDate paymentDate;
    private BigDecimal openingBalance;
    private BigDecimal payment;                      
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal closingBalance;

    // constructor

    public PaymentPeriod() {
    }

    // create the paymentPeriod object with details of a single payment in the schedule like  number of months , date of payment , interest , monthly payment and so on as its attributes
    public PaymentPeriod(int periodNumber, LocalDate paymentDate, BigDecimal openingBalance,
                          BigDecimal payment, BigDecimal principal, BigDecimal interest,
                          BigDecimal closingBalance) {
        this.periodNumber = periodNumber;
        this.paymentDate = paymentDate;
        this.openingBalance = openingBalance;
        this.payment = payment;
        this.principal = principal;
        this.interest = interest;
        this.closingBalance = closingBalance;
    }

    // get and set the number of payment i.e. the 1st payment or the nth payment 

    public int getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(int periodNumber) {
        this.periodNumber = periodNumber;
    }

    // get and set the scheduled date of payment for each period 

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    // get and set the total remaining balance of the loan before the current(the next installment to be paid) payment 
    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    // get and set the total amount of payment made for the particular period

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    // get ad set the amount of payment that counts toward sthe loan principal amount 

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    // gte and set the interst on the particular paymaent  

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    //get and set the remaining balance on the date of the payment made i.e. after making the payment 

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }
}