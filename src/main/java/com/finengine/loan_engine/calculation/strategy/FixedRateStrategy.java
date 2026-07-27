package com.finengine.loan_engine.calculation.strategy;

import com.finengine.loan_engine.model.LoanRequest;
import com.finengine.loan_engine.model.LoanType;
import com.finengine.loan_engine.model.PaymentPeriod;
import com.finengine.loan_engine.model.PaymentSchedule;
import com.finengine.loan_engine.util.FinancialMath;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class FixedRateStrategy implements LoanCalculationStrategy {

    @Override
    public PaymentSchedule calculate(LoanRequest request) {
        BigDecimal principal = request.getPrincipal();
        int termMonths = request.getTermMonths();
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();

        BigDecimal monthlyRate = FinancialMath.divide(request.getAnnualInterestRate(), BigDecimal.valueOf(1200));
        BigDecimal monthlyPayment = FinancialMath.calculateMonthlyPayment(principal, monthlyRate, termMonths);

        PaymentSchedule schedule = new PaymentSchedule();
        BigDecimal balance = principal;

        for (int period = 1; period <= termMonths; period++) {
            BigDecimal interest = FinancialMath.toMonetary(balance.multiply(monthlyRate));
            BigDecimal principalPortion = monthlyPayment.subtract(interest);
            BigDecimal payment = monthlyPayment;

            if (period == termMonths) {
                // if its the final period,pay off whatever balance remains exactly,
                // absorbing any rounding errors accumulated over the schedule.
                principalPortion = balance;
                payment = principalPortion.add(interest);
            }

            BigDecimal closingBalance = balance.subtract(principalPortion);

            schedule.addPeriod(new PaymentPeriod(
                    period,
                    startDate.plusMonths(period),
                    balance,
                    FinancialMath.toMonetary(payment),
                    FinancialMath.toMonetary(principalPortion),
                    interest,
                    FinancialMath.toMonetary(closingBalance)
            ));

            balance = closingBalance;
        }

        return schedule;
    }
    
    @Override
    public LoanType getSupportedType() {
        return LoanType.FIXED;
    }
}