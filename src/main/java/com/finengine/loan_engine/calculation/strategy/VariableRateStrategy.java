package com.finengine.loan_engine.calculation.strategy;

import com.finengine.loan_engine.model.*;
import com.finengine.loan_engine.util.FinancialMath;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class VariableRateStrategy implements LoanCalculationStrategy {

    @Override
    public PaymentSchedule calculate(LoanRequest request) {
        List<RateChange> changes = request.getRateChangeSchedule() != null
        ? request.getRateChangeSchedule()
        : List.of(); 
        int totalTermMonths = request.getTermMonths();
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();

        PaymentSchedule schedule = new PaymentSchedule();
        BigDecimal currentBalance = request.getPrincipal();
        int periodCounter = 1;

        // Segment 0 always starts with the loan's original rate
        BigDecimal currentRate = request.getAnnualInterestRate();

        for (int segmentIndex = 0; segmentIndex <= changes.size(); segmentIndex++) {
            int segmentEndMonth = (segmentIndex < changes.size())
                    ? changes.get(segmentIndex).getEffectiveMonth() - 1
                    : totalTermMonths;

            BigDecimal monthlyRate = FinancialMath.divide(currentRate, BigDecimal.valueOf(1200));
            int remainingTermMonths = totalTermMonths - (periodCounter - 1);
            BigDecimal segmentPayment = FinancialMath.calculateMonthlyPayment(currentBalance, monthlyRate, remainingTermMonths);

            for (int month = periodCounter; month <= segmentEndMonth; month++) {
                BigDecimal interest = FinancialMath.toMonetary(currentBalance.multiply(monthlyRate));
                BigDecimal principalPortion = segmentPayment.subtract(interest);
                BigDecimal payment = segmentPayment;

                if (month == totalTermMonths) {
                    // Only the true final period of the WHOLE loan absorbs rounding drift
                    principalPortion = currentBalance;
                    payment = principalPortion.add(interest);
                }

                BigDecimal closingBalance = currentBalance.subtract(principalPortion);

                schedule.addPeriod(new PaymentPeriod(
                        month,
                        startDate.plusMonths(month),
                        currentBalance,
                        FinancialMath.toMonetary(payment),
                        FinancialMath.toMonetary(principalPortion),
                        interest,
                        FinancialMath.toMonetary(closingBalance)
                ));

                currentBalance = closingBalance;
            }

            periodCounter = segmentEndMonth + 1;

            if (segmentIndex < changes.size()) {
                currentRate = changes.get(segmentIndex).getNewAnnualRate();
            }
        }

        return schedule;
    }

    @Override
    public LoanType getSupportedType() {
        return LoanType.VARIABLE;
    }
}