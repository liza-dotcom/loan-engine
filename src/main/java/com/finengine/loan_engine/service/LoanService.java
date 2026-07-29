package com.finengine.loan_engine.service;

import com.finengine.loan_engine.calculation.strategy.LoanStrategyRegistry;
import com.finengine.loan_engine.model.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanService {

    private final LoanStrategyRegistry strategyRegistry;

    public LoanService(LoanStrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;
    }

    public PaymentSchedule calculateSchedule(LoanRequest request) {
        return strategyRegistry.getStrategy(request.getLoanType()).calculate(request);
    }

    public LoanSummary calculateSummary(LoanRequest request) {
        PaymentSchedule schedule = calculateSchedule(request);
        List<PaymentPeriod> periods = schedule.getPeriods();

        BigDecimal totalPayment = periods.stream()
                .map(PaymentPeriod::getPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPrincipal = periods.stream()
                .map(PaymentPeriod::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalInterest = periods.stream()
                .map(PaymentPeriod::getInterest)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Placeholder for actual mixed effective rates for variable-rate loans
        // needs a weighted calculation across segments. Using the nominal
        // starting rate for now.
        BigDecimal effectiveRate = request.getAnnualInterestRate();

        return new LoanSummary(totalPayment, totalInterest, totalPrincipal, effectiveRate);
    }

    public LoanComparison compare(LoanRequest first, LoanRequest second) {
        LoanSummary firstSummary = calculateSummary(first);
        LoanSummary secondSummary = calculateSummary(second);
        BigDecimal interestDifference = firstSummary.getTotalInterest().subtract(secondSummary.getTotalInterest());

        return new LoanComparison(firstSummary, secondSummary, interestDifference);
    }
}