package com.finengine.loan_engine.controller;

import com.finengine.loan_engine.model.*;
import com.finengine.loan_engine.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loans")
@Tag(name = "Loan Calculation", description = "Endpoints for calculating amortization schedules across fixed, variable, and interest-only loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @Operation(summary = "Calculate full amortization schedule",
               description = "Returns a complete period-by-period breakdown for the specified loan type (FIXED, VARIABLE, or INTEREST_ONLY)")
    @ApiResponse(responseCode = "200", description = "Schedule calculated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid loan parameters")
    @PostMapping("/calculate")
    public PaymentSchedule calculate(@Valid @RequestBody LoanRequest request) {
        return loanService.calculateSchedule(request);
    }

    @Operation(summary = "Calculate loan summary",
               description = "Returns aggregate totals (total payment, total interest, total principal) without the full period-by-period schedule")
    @ApiResponse(responseCode = "200", description = "Summary calculated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid loan parameters")
    @PostMapping("/summary")
    public LoanSummary summary(@Valid @RequestBody LoanRequest request) {
        return loanService.calculateSummary(request);
    }

    @Operation(summary = "Compare two loan scenarios",
               description = "Calculates summaries for two different loans side by side and returns the interest cost difference between them")
    @ApiResponse(responseCode = "200", description = "Comparison calculated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid loan parameters on one or both loans")
    @PostMapping("/compare")
    public LoanComparison compare(@Valid @RequestBody LoanComparisonRequest request) {
        return loanService.compare(request.getFirstLoan(), request.getSecondLoan());
    }
}