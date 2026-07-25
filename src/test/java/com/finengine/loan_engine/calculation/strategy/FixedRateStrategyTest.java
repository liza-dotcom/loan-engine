package com.finengine.loan_engine.calculation.strategy;

import com.finengine.loan_engine.model.LoanRequest;
import com.finengine.loan_engine.model.LoanType;
import com.finengine.loan_engine.model.PaymentPeriod;
import com.finengine.loan_engine.model.PaymentSchedule;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FixedRateStrategyTest {

    private final FixedRateStrategy strategy = new FixedRateStrategy();

    @Test
    void standardThirtyYearMortgageProducesCorrectMonthlyPayment() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(300000),
                BigDecimal.valueOf(5),
                360,
                LoanType.FIXED,
                LocalDate.of(2026, 1, 1)
        );

        PaymentSchedule schedule = strategy.calculate(request);
        BigDecimal firstPayment = schedule.getPeriods().get(0).getPayment();

        // Actual correct result for $300,000 at 5% over 30 years = $1,610.46 approx.
        assertEquals(0, BigDecimal.valueOf(1610.46).compareTo(firstPayment),
                "Expected ~$1610.46, got " + firstPayment);
    }

    @Test
    void oneMonthLoanPaysOffEntirelyInOnePeriod() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(5),
                1,
                LoanType.FIXED,
                LocalDate.of(2026, 1, 1)
        );

        PaymentSchedule schedule = strategy.calculate(request);

        assertEquals(1, schedule.getPeriods().size());
        assertEquals(0, BigDecimal.ZERO.compareTo(schedule.getPeriods().get(0).getClosingBalance()));
    }

    @Test
    void zeroInterestLoanSplitsPrincipalEvenly() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(1200),
                BigDecimal.ZERO,
                12,
                LoanType.FIXED,
                LocalDate.of(2026, 1, 1)
        );

        PaymentSchedule schedule = strategy.calculate(request);
        BigDecimal firstPayment = schedule.getPeriods().get(0).getPayment();

        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(firstPayment));
    }

    @Test
    void totalPaymentsEqualPrincipalPlusInterest() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(50000),
                BigDecimal.valueOf(6),
                60,
                LoanType.FIXED,
                LocalDate.of(2026, 1, 1)
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

        // MathemATICAL TEST should verufy that total payments = total principal + total interest
        assertEquals(0, totalPayments.compareTo(totalPrincipal.add(totalInterest)));
    }

    @Test
    void finalPeriodClosingBalanceIsExactlyZero() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(300000),
                BigDecimal.valueOf(5),
                360,
                LoanType.FIXED,
                LocalDate.of(2026, 1, 1)
        );

        PaymentSchedule schedule = strategy.calculate(request);
        List<PaymentPeriod> periods = schedule.getPeriods();
        BigDecimal lastClosingBalance = periods.get(periods.size() - 1).getClosingBalance();

        assertEquals(0, BigDecimal.ZERO.compareTo(lastClosingBalance));
    }
}