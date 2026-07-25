package com.finengine.loan_engine.calculation.strategy;

import com.finengine.loan_engine.model.LoanRequest;
import com.finengine.loan_engine.model.LoanType;
import com.finengine.loan_engine.model.PaymentSchedule;

public interface LoanCalculationStrategy {

    PaymentSchedule calculate(LoanRequest request);

    LoanType getSupportedType();
}