package com.finengine.loan_engine.model;

import java.util.ArrayList;
import java.util.List;

public class PaymentSchedule {

    // declare an arraylist to store all the payment sof a loan to make ammortization schedule
    private List<PaymentPeriod> periods;

    public PaymentSchedule() {
        this.periods = new ArrayList<>();
    }

    // create a payment schedule using current info/list of payment periods

    public PaymentSchedule(List<PaymentPeriod> periods) {
        this.periods = periods;
    }

    public List<PaymentPeriod> getPeriods() {
        return periods;
    }

    public void setPeriods(List<PaymentPeriod> periods) {
        this.periods = periods;
    }

    public void addPeriod(PaymentPeriod period) {
        this.periods.add(period);
    }
}