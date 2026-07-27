package com.finengine.loan_engine.calculation.strategy;

import com.finengine.loan_engine.model.*;
import com.finengine.loan_engine.util.FinancialMath;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class InterestOnlyStrategy implements LoanCalculationStrategy {

    @Override
    public PaymentSchedule calculate(LoanRequest request) {
        BigDecimal principal = request.getPrincipal();
        int interestOnlyMonths = request.getInterestOnlyMonths();
        int totalTermMonths = request.getTermMonths();
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();
        BigDecimal monthlyRate = FinancialMath.divide(request.getAnnualInterestRate(), BigDecimal.valueOf(1200));

        PaymentSchedule schedule = new PaymentSchedule();
        BigDecimal balance = principal;

        // Phase 1- interest-only phase and balance never reduces
        for (int month = 1; month <= interestOnlyMonths; month++) {
            BigDecimal interest = FinancialMath.toMonetary(balance.multiply(monthlyRate));
            BigDecimal principalPortion = BigDecimal.ZERO;
            BigDecimal payment = interest;

            schedule.addPeriod(new PaymentPeriod(
                    month, startDate.plusMonths(month), balance,
                    payment, principalPortion, interest, balance
            ));
        }

        // Phase 2- full principal is still unpaid, but only totalTerm-interestOnlyMonths are left to pay it off
       // Phase 2: amortize remaining principal, OR balloon payment if no amortization months remain
int remainingTermMonths = totalTermMonths - interestOnlyMonths;

if (remainingTermMonths > 0) {
    BigDecimal payment = FinancialMath.calculateMonthlyPayment(balance, monthlyRate, remainingTermMonths);

    for (int month = interestOnlyMonths + 1; month <= totalTermMonths; month++) {
        BigDecimal interest = FinancialMath.toMonetary(balance.multiply(monthlyRate));
        BigDecimal principalPortion = payment.subtract(interest);
        BigDecimal actualPayment = payment;

        if (month == totalTermMonths) {
            principalPortion = balance;
            actualPayment = principalPortion.add(interest);
        }

        BigDecimal closingBalance = balance.subtract(principalPortion);

        schedule.addPeriod(new PaymentPeriod(
                month, startDate.plusMonths(month), balance,
                FinancialMath.toMonetary(actualPayment), FinancialMath.toMonetary(principalPortion),
                interest, FinancialMath.toMonetary(closingBalance)
        ));

        balance = closingBalance;
    }
} else {
    // Whole loan is interest-only: the final period already exists from Phase 1
    // as a pure-interest period. Replace it with a balloon payment that also
    // clears the entire remaining principal in one lump sum.
    PaymentPeriod finalInterestOnlyPeriod = schedule.getPeriods().remove(schedule.getPeriods().size() - 1);
    BigDecimal interest = finalInterestOnlyPeriod.getInterest();
    BigDecimal balloonPayment = balance.add(interest);

    schedule.addPeriod(new PaymentPeriod(
            finalInterestOnlyPeriod.getPeriodNumber(),
            finalInterestOnlyPeriod.getPaymentDate(),
            finalInterestOnlyPeriod.getOpeningBalance(),
            FinancialMath.toMonetary(balloonPayment),
            FinancialMath.toMonetary(balance),
            interest,
            BigDecimal.ZERO
    ));
}

        return schedule;
    }

    @Override
    public LoanType getSupportedType() {
        return LoanType.INTEREST_ONLY;
    }
}