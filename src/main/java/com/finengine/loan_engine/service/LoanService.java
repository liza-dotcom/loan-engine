package com.finengine.loan_engine.service;

import com.finengine.loan_engine.calculation.strategy.FixedRateStrategy;
import com.finengine.loan_engine.model.LoanRequest;
import com.finengine.loan_engine.model.PaymentSchedule;
import org.springframework.stereotype.Service;

@Service
public class LoanService {

    private final FixedRateStrategy fixedRateStrategy;

    public LoanService(FixedRateStrategy fixedRateStrategy) {
        this.fixedRateStrategy = fixedRateStrategy;
    }

    public PaymentSchedule calculateSchedule(LoanRequest request) {
        // TODO Stage 2: replace with LoanStrategyRegistry.getStrategy(request.getLoanType())
        return fixedRateStrategy.calculate(request);
    }
}