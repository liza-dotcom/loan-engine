package com.finengine.loan_engine.controller;

import com.finengine.loan_engine.model.LoanRequest;
import com.finengine.loan_engine.model.PaymentSchedule;
import com.finengine.loan_engine.service.LoanService;
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
    public PaymentSchedule calculate(@RequestBody LoanRequest request) {
        return loanService.calculateSchedule(request);
    }
}