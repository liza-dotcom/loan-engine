package com.finengine.loan_engine.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoanControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void calculateEndpointReturnsFullScheduleForValidFixedRateLoan() throws Exception {
        String requestBody = """
                {
                    "principal": 300000,
                    "annualInterestRate": 5,
                    "termMonths": 360,
                    "loanType": "FIXED",
                    "startDate": "2026-01-01"
                }
                """;

        mockMvc.perform(post("/api/v1/loans/calculate")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periods").isArray())
                .andExpect(jsonPath("$.periods.length()").value(360))
                .andExpect(jsonPath("$.periods[0].payment").value(1610.46));
    }

    @Test
    void calculateEndpointRejectsNegativePrincipalWithFieldError() throws Exception {
        String requestBody = """
                {
                    "principal": -5000,
                    "annualInterestRate": 5,
                    "termMonths": 360,
                    "loanType": "FIXED",
                    "startDate": "2026-01-01"
                }
                """;

        mockMvc.perform(post("/api/v1/loans/calculate")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.principal").value("Principal must be positive"));
    }

    @Test
    void summaryEndpointReturnsAggregateTotals() throws Exception {
        String requestBody = """
                {
                    "principal": 300000,
                    "annualInterestRate": 5,
                    "termMonths": 360,
                    "loanType": "FIXED",
                    "startDate": "2026-01-01"
                }
                """;

        mockMvc.perform(post("/api/v1/loans/summary")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrincipal").value(300000.00))
                .andExpect(jsonPath("$.totalPayment").exists())
                .andExpect(jsonPath("$.totalInterest").exists());
    }

    @Test
    void compareEndpointReturnsBothSummariesAndDifference() throws Exception {
        String requestBody = """
                {
                    "firstLoan": {"principal": 300000, "annualInterestRate": 5, "termMonths": 360, "loanType": "FIXED", "startDate": "2026-01-01"},
                    "secondLoan": {"principal": 300000, "annualInterestRate": 4, "termMonths": 360, "loanType": "VARIABLE", "startDate": "2026-01-01", "rateChangeSchedule": [{"effectiveMonth": 121, "newAnnualRate": 6}]}
                }
                """;

        mockMvc.perform(post("/api/v1/loans/compare")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstLoan").exists())
                .andExpect(jsonPath("$.secondLoan").exists())
                .andExpect(jsonPath("$.interestDifference").exists());
    }

    @Test
    void calculateEndpointRejectsInvalidLoanType() throws Exception {
        String requestBody = """
                {
                    "principal": 300000,
                    "annualInterestRate": 5,
                    "termMonths": 360,
                    "loanType": "BALLOON",
                    "startDate": "2026-01-01"
                }
                """;

        mockMvc.perform(post("/api/v1/loans/calculate")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

        @Test
    void calculateRejectsVariableLoanWithoutRateSchedule() throws Exception {
        String requestBody = """
                {
                    "principal": 400000,
                    "annualInterestRate": 4.2,
                    "termMonths": 300,
                    "loanType": "VARIABLE"
                }
                """;

        mockMvc.perform(post("/api/v1/loans/calculate")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.rateScheduleValidForType")
                        .value("VARIABLE loans need a rateChangeSchedule with at least one rate change"));
    }

    @Test
    void compareRejectsMissingSecondLoan() throws Exception {
        String requestBody = """
                {
                    "firstLoan": {
                        "principal": 400000,
                        "annualInterestRate": 4.8,
                        "termMonths": 300,
                        "loanType": "FIXED"
                    }
                }
                """;

        mockMvc.perform(post("/api/v1/loans/compare")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.secondLoan").value("Second loan is required"));
    }

    @Test
    void compareValidatesFieldsInsideEachLoan() throws Exception {
        String requestBody = """
                {
                    "firstLoan": {
                        "principal": 400000,
                        "annualInterestRate": 4.8,
                        "termMonths": 300,
                        "loanType": "FIXED"
                    },
                    "secondLoan": {
                        "principal": 400000,
                        "annualInterestRate": 4.2,
                        "termMonths": 300,
                        "loanType": "VARIABLE"
                    }
                }
                """;

        mockMvc.perform(post("/api/v1/loans/compare")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors['secondLoan.rateScheduleValidForType']").exists());
    }
}
