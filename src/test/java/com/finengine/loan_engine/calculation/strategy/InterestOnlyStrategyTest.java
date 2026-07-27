package com.finengine.loan_engine.calculation.strategy;

import com.finengine.loan_engine.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterestOnlyStrategyTest {

    private final InterestOnlyStrategy strategy = new InterestOnlyStrategy();

    @Test
    void principalUnchangedThroughoutInterestOnlyPhase() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(300000),
                BigDecimal.valueOf(5),
                360,
                LoanType.INTEREST_ONLY,
                LocalDate.of(2026, 1, 1),
                60
        );

        PaymentSchedule schedule = strategy.calculate(request);
        List<PaymentPeriod> periods = schedule.getPeriods();

        // Check every period in the interest-only phase, not just the last one
        for (int i = 0; i < 60; i++) {
            PaymentPeriod period = periods.get(i);
            assertEquals(0, BigDecimal.valueOf(300000).compareTo(period.getClosingBalance()),
                    "Balance moved during interest-only period at month " + (i + 1));
            assertEquals(0, BigDecimal.ZERO.compareTo(period.getPrincipal()),
                    "Principal portion should be zero during interest-only period at month " + (i + 1));
        }
    }

    @Test
    void amortizationPhasePaysOffExactlyByTermEnd() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(300000),
                BigDecimal.valueOf(5),
                360,
                LoanType.INTEREST_ONLY,
                LocalDate.of(2026, 1, 1),
                60
        );

        PaymentSchedule schedule = strategy.calculate(request);
        List<PaymentPeriod> periods = schedule.getPeriods();
        BigDecimal lastClosingBalance = periods.get(periods.size() - 1).getClosingBalance();

        assertEquals(0, BigDecimal.ZERO.compareTo(lastClosingBalance));
    }

    @Test
    void paymentJumpsWhenAmortizationPhaseBegins() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(300000),
                BigDecimal.valueOf(5),
                360,
                LoanType.INTEREST_ONLY,
                LocalDate.of(2026, 1, 1),
                60
        );

        PaymentSchedule schedule = strategy.calculate(request);
        List<PaymentPeriod> periods = schedule.getPeriods();

        BigDecimal lastInterestOnlyPayment = periods.get(59).getPayment();  // month 60
        BigDecimal firstAmortizingPayment = periods.get(60).getPayment();   // month 61

        // Interest-only payment should be strictly smaller than the amortizing
        // payment, since month 61 must now pay off the full principal in
        // fewer remaining months than the original term.
        assertEquals(true, firstAmortizingPayment.compareTo(lastInterestOnlyPayment) > 0);
    }

    @Test
    void wholeLoanInterestOnlyMeansPrincipalNeverReducesExceptFinalPayoff() {
        // Edge case: interestOnlyMonths == termMonths means every period except
        // the very last one is pure interest; the doc calls this "perpetual
        // interest, no principal repayment" until the strategy forces payoff
        // at month == totalTermMonths (same final-period rule as FixedRateStrategy).
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(100000),
                BigDecimal.valueOf(5),
                24,
                LoanType.INTEREST_ONLY,
                LocalDate.of(2026, 1, 1),
                24
        );

        PaymentSchedule schedule = strategy.calculate(request);
        List<PaymentPeriod> periods = schedule.getPeriods();

        // Months 1-23: principal untouched
        for (int i = 0; i < 23; i++) {
            assertEquals(0, BigDecimal.valueOf(100000).compareTo(periods.get(i).getClosingBalance()));
        }
        // Month 24: balloon payment clears the entire remaining principal
        PaymentPeriod finalPeriod = periods.get(23);
        assertEquals(0, BigDecimal.valueOf(100000).compareTo(finalPeriod.getPrincipal()));
        assertEquals(0, BigDecimal.ZERO.compareTo(finalPeriod.getClosingBalance()));
    }

    @Test
    void totalPaymentsEqualPrincipalPlusInterest() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(150000),
                BigDecimal.valueOf(6),
                180,
                LoanType.INTEREST_ONLY,
                LocalDate.of(2026, 1, 1),
                24
        );

        PaymentSchedule schedule = strategy.calculate(request);
        List<PaymentPeriod> periods = schedule.getPeriods();

        BigDecimal totalPayments = periods.stream()
                .map(PaymentPeriod::getPayment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPrincipal = periods.stream()
                .map(PaymentPeriod::getPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalInterest = periods.stream()
                .map(PaymentPeriod::getInterest)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(0, totalPayments.compareTo(totalPrincipal.add(totalInterest)));
    }
}