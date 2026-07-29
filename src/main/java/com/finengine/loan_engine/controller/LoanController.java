package com.finengine.loan_engine.controller;

import com.finengine.loan_engine.model.*;
import com.finengine.loan_engine.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/calculate")
    public PaymentSchedule calculate(@Valid @RequestBody LoanRequest request) {
        return loanService.calculateSchedule(request);
    }

    @PostMapping("/summary")
    public LoanSummary summary(@Valid @RequestBody LoanRequest request) {
        return loanService.calculateSummary(request);
    }

    @PostMapping("/compare")
    public LoanComparison compare(@Valid @RequestBody LoanComparisonRequest request) {
        return loanService.compare(request.getFirstLoan(), request.getSecondLoan());
    }
}