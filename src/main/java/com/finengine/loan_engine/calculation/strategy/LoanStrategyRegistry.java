package com.finengine.loan_engine.calculation.strategy;

import com.finengine.loan_engine.exception.UnsupportedLoanTypeException;
import com.finengine.loan_engine.model.LoanType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LoanStrategyRegistry {

    private final Map<LoanType, LoanCalculationStrategy> strategies;

    public LoanStrategyRegistry(List<LoanCalculationStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        LoanCalculationStrategy::getSupportedType,
                        strategy -> strategy
                ));
    }

    public LoanCalculationStrategy getStrategy(LoanType type) {
    LoanCalculationStrategy strategy = strategies.get(type);
    if (strategy == null) {
        throw new UnsupportedLoanTypeException(type);
    }
    return strategy;
}
}