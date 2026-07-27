package com.finengine.loan_engine.calculation.strategy;

import com.finengine.loan_engine.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class VariableRateStrategyTest {

    private final VariableRateStrategy strategy = new VariableRateStrategy();

    @Test
    void scheduleCoversEveryMonthOfTheTerm() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(200000),
                BigDecimal.valueOf(4),
                360,
                LoanType.VARIABLE,
                LocalDate.of(2026, 1, 1),
                List.of(new RateChange(121, BigDecimal.valueOf(6)))
        );

        PaymentSchedule schedule = strategy.calculate(request);

        assertEquals(360, schedule.getPeriods().size());
    }

    @Test
    void paymentAmountChangesExactlyAtRateChangeBoundary() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(200000),
                BigDecimal.valueOf(4),
                360,
                LoanType.VARIABLE,
                LocalDate.of(2026, 1, 1),
                List.of(new RateChange(121, BigDecimal.valueOf(6)))
        );

        PaymentSchedule schedule = strategy.calculate(request);
        List<PaymentPeriod> periods = schedule.getPeriods();

        BigDecimal lastPaymentBeforeChange = periods.get(119).getPayment(); // period 120
        BigDecimal firstPaymentAfterChange = periods.get(120).getPayment(); // period 121

        // Rate jumped from 4% to 6% at month 121, so the recalculated
        // payment must differ from the prior segment's payment.
        assertNotEquals(0, lastPaymentBeforeChange.compareTo(firstPaymentAfterChange));

        // Within segment 1 itself, payment should be constant (months 1-120 all equal)
        assertEquals(0, periods.get(0).getPayment().compareTo(periods.get(119).getPayment()));
    }

    @Test
    void totalPaymentsEqualPrincipalPlusInterestAcrossSegments() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(200000),
                BigDecimal.valueOf(4),
                360,
                LoanType.VARIABLE,
                LocalDate.of(2026, 1, 1),
                List.of(new RateChange(121, BigDecimal.valueOf(6)))
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

    @Test
    void finalPeriodClosingBalanceIsExactlyZeroDespiteRateChanges() {
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(200000),
                BigDecimal.valueOf(4),
                360,
                LoanType.VARIABLE,
                LocalDate.of(2026, 1, 1),
                List.of(
                        new RateChange(121, BigDecimal.valueOf(6)),
                        new RateChange(241, BigDecimal.valueOf(5))
                )
        );

        PaymentSchedule schedule = strategy.calculate(request);
        List<PaymentPeriod> periods = schedule.getPeriods();
        BigDecimal lastClosingBalance = periods.get(periods.size() - 1).getClosingBalance();

        assertEquals(0, BigDecimal.ZERO.compareTo(lastClosingBalance));
    }

    @Test
    void noRateChangesBehavesIdenticallyToFixedRate() {
        // Sanity cross-check: an empty rate change list means the whole loan
        // is one segment — this should produce the exact same first payment
        // as FixedRateStrategy given identical inputs.
        LoanRequest request = new LoanRequest(
                BigDecimal.valueOf(300000),
                BigDecimal.valueOf(5),
                360,
                LoanType.VARIABLE,
                LocalDate.of(2026, 1, 1),
                List.of()
        );

        PaymentSchedule schedule = strategy.calculate(request);
        BigDecimal firstPayment = schedule.getPeriods().get(0).getPayment();

        assertEquals(0, BigDecimal.valueOf(1610.46).compareTo(firstPayment));
    }
}