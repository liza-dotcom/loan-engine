# Financial Calculation Engine

A Java/Spring Boot REST API for calculating loan amortization schedules for three loan structures: 
1. Fixed-rate
2. Variable-rate
3. Interest-only 

The loan engine is designed with the mathematical precision, test coverage, and production-style infrastructure of a real financial service.

**[Live API — Swagger UI](http://loan-engine-env.eba-xvp7cmzy.us-east-2.elasticbeanstalk.com/swagger-ui.html)** | [GitHub](https://github.com/liza-dotcom/loan-engine)

## Overview

This engine models three structurally different loan types, each with its own calculation logic and edge cases:

- **Fixed-rate** — a constant rate and payment for the full term.
- **Variable-rate** — the rate changes at defined points in the loan. Each change requires recalculating the payment against the *remaining balance* and the *remaining total term*, not the length of the new segment. Using the segment length instead produces a wrong payoff schedule.
- **Interest-only** — payments cover interest only for an initial period, with the balance untouched, followed by full amortization over whatever term remains. A loan that is interest-only for its entire term is handled as a balloon payment on the final period rather than left with an unresolved balance.

The API is intentionally backend-only. The interactive Swagger UI link above serves as the interface for exploring and testing the engine directly.

## Design decisions

**BigDecimal, not `double`, throughout.** Floating-point arithmetic introduces representation errors that compound across a multi-hundred-period schedule. All monetary values use `BigDecimal`, with intermediate calculations carried to extended precision and rounded to two decimal places only at the point of final output, using `RoundingMode.HALF_EVEN`. Banker's rounding is the standard in financial systems because, unlike `HALF_UP`, it doesn't introduce a directional bias when applied at scale.

**Strategy pattern for loan type dispatch.** Each loan type is implemented as an independent class behind a shared `LoanCalculationStrategy` interface. A registry component uses Spring's dependency injection to auto-discover all implementations at startup and route requests by loan type — adding a new loan type requires no changes to existing controller, service, or registry code.

**Rounding drift correction.** Rounding to the cent on every period of a 360-month schedule accumulates a small discrepancy by the final period. The engine's final period absorbs this drift explicitly, so every schedule closes at exactly $0.00 rather than leaving a stray fraction of a cent.

**An edge case.** A loan configured as 100% interest-only i.e. when interest period equals the entire loan term, produces a division-by-zero in the standard amortization formula, since the number of months available to amortize any principal is zero. This was caught by a dedicated test case rather than in production, and resolved by treating the final period as a balloon payment — the loan's principal is fully due at term end rather than left open-ended.

**Input validation, not just calculation correctness:** Every request is validated using **Jakarta Bean Validation** annotations before any calculation is performed. Invalid input—such as negative loan principals, out-of-range interest rates, invalid loan terms, or missing required fields is rejected with a structured **HTTP 400 Bad Request** response that clearly identifies the affected field and the reason for the validation failure, preventing malformed data from ever reaching the calculation engine.

## API Reference

Full interactive documentation with request/response schemas: **[Swagger UI](http://loan-engine-env.eba-xvp7cmzy.us-east-2.elasticbeanstalk.com/swagger-ui.html)**

| Endpoint | Method | Description |
|---|---|---|
| `/api/v1/loans/calculate` | POST | Full period-by-period amortization schedule |
| `/api/v1/loans/summary` | POST | Aggregate totals — total payment, total interest, total principal |
| `/api/v1/loans/compare` | POST | Side-by-side comparison of two loan scenarios, including total interest difference |
| `/api/v1/health` | GET | Health check |

## Architecture

```
Controller  →  Service  →  Strategy Registry  →  Strategy (Fixed / Variable / Interest-Only)
  (HTTP)       (orchestration)   (routes by LoanType)      (calculation logic)
```

- `controller/` — HTTP layer with no business logic
- `service/` — orchestrates calculation and derives summary statistics from a completed schedule
- `calculation/strategy/` — the engine itself: one interface, three independent implementations, one registry
- `model/` — request and response data classes
- `exception/` — global exception handling, returns structured error responses (timestamp, status, field-level messages) instead of raw stack traces
- `util/` — centralized `BigDecimal` arithmetic (scale, rounding mode), so no calculation logic scatters raw precision handling

## Infrastructure & Deployment

- **Containerized with Docker** — a multi-stage-free `Dockerfile` builds a minimal runtime image (`eclipse-temurin` JRE on Alpine Linux), and `docker-compose.yml` starts the full stack with a single command.
- **CI via GitHub Actions** — every push and pull request triggers a pipeline that runs the full test suite and builds the application; the build fails fast if any test fails, before anything reaches deployment.
- **Deployed to AWS Elastic Beanstalk** on the Docker platform, running the same container image locally and in production — configured with its own service role and EC2 instance profile via IAM, on a single free-tier-eligible instance.

## Running locally

```bash
docker compose up
```

Builds the image and starts the API on `localhost:8080`.


## Testing

21 tests, 16 unit and 5 integration, all passing:

- Unit tests across all three strategies, covering standard cases, edge cases (zero interest, single-month terms, fully interest-only loans), and mathematical invariants (total payments equal principal plus interest; every schedule closes at exactly $0.00)
- Integration tests (MockMvc) covering the full HTTP stack — routing, request validation, and error handling — rather than calculation logic in isolation

## Stack

Java 25 · Spring Boot 4 · Maven · JUnit 5 · Docker · GitHub Actions (CI/CD) · AWS Elastic Beanstalk · SpringDoc/OpenAPI
