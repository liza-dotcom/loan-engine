package com.finengine.loan_engine.service;

import com.finengine.loan_engine.calculation.strategy.LoanStrategyRegistry;
import com.finengine.loan_engine.model.LoanRequest;
import com.finengine.loan_engine.model.PaymentSchedule;
import org.springframework.stereotype.Service;

@Service
public class LoanService {

    private final LoanStrategyRegistry strategyRegistry;

    public LoanService(LoanStrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;
    }

    public PaymentSchedule calculateSchedule(LoanRequest request) {
        return strategyRegistry.getStrategy(request.getLoanType()).calculate(request);
    }
}