package com.finengine.loan_engine.exception;

import com.finengine.loan_engine.model.LoanType;

public class UnsupportedLoanTypeException extends RuntimeException {
    public UnsupportedLoanTypeException(LoanType type) {
        super("No calculation strategy registered for loan type: " + type);
    }
}