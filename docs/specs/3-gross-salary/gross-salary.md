# GrossSalary + CalculationResult — TDD Cycle 3

## Description

Defines the domain's immutable value types. `GrossSalary` encapsulates and validates the gross salary as a Java record, preventing a negative or zero value from circulating through the system. `CalculationResult` aggregates the four fields of a net salary assessment — without its own calculation logic.

---

## Implementation Checklist

### GrossSalary
- [ ] Create `src/test/java/com/pfc/tdd/calculator/domain/GrossSalaryTest.java` (RED)
- [ ] Create `src/main/java/com/pfc/tdd/calculator/domain/GrossSalary.java` as a `record` (GREEN)
- [ ] Add canonical constructor with validation: throw `IllegalArgumentException` if `value <= 0`
- [ ] **Never use `double`** — field is `BigDecimal`

### CalculationResult
- [ ] Create `src/test/java/com/pfc/tdd/calculator/domain/CalculationResultTest.java` (RED)
- [ ] Create `src/main/java/com/pfc/tdd/calculator/domain/CalculationResult.java` as a `record` (GREEN)
- [ ] Four fields: `gross`, `inss`, `irrf`, `net` — all `BigDecimal`, non-null

---

## GrossSalary

### Definition

```java
public record GrossSalary(BigDecimal value) {
    public GrossSalary {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Gross salary must be greater than zero");
        }
    }
}
```

### Test Cases

| Input         | Expected Result                             |
|---------------|---------------------------------------------|
| `3000.00`     | Record created successfully                 |
| `0.01`        | Record created successfully (minimum valid) |
| `0.00`        | `IllegalArgumentException`                  |
| `-100.00`     | `IllegalArgumentException`                  |
| `null`        | `NullPointerException` (default record behavior) |

---

## CalculationResult

### Definition

```java
public record CalculationResult(
    BigDecimal gross,
    BigDecimal inss,
    BigDecimal irrf,
    BigDecimal net
) {}
```

### Fields

| Field     | Type       | Description                                 |
|-----------|------------|---------------------------------------------|
| `gross`   | BigDecimal | Original gross salary                       |
| `inss`    | BigDecimal | Calculated INSS contribution                |
| `irrf`    | BigDecimal | Calculated IRRF                             |
| `net`     | BigDecimal | Net salary (`gross − inss − irrf`)          |

> `CalculationResult` **does not calculate** the net salary — it receives the four values already prepared from `SalaryNetCalculator`.

### Test Cases

| gross       | inss      | irrf      | net         |
|-------------|-----------|-----------|-------------|
| R$ 5.000,00 | R$ 509,60 | R$ 479,00 | R$ 4.011,40 |

---

## Rules

- `GrossSalary.value` must be strictly greater than zero — zero and negative values throw `IllegalArgumentException`.
- Both records are **immutable** by definition — do not add setters or mutable fields.
- `CalculationResult` has no internal logic — it is just a data aggregator.

---

## Object Calisthenics Principles applied

| Rule  | Application |
|-------|-----------|
| OC #3 | `GrossSalary` encapsulates `BigDecimal` with validation; `inss`/`irrf` are not encapsulated (YAGNI) |
| OC #7 | `GrossSalary` canonical constructor ≤ 3 lines |
| OC #8 | `GrossSalary`: 1 field. `CalculationResult`: 4 fields — justified exception (it's a result DTO) |

---

## Assumptions

- `inss` and `irrf` are not encapsulated in their own wrappers because there is no concrete risk of accidental exchange given the typed use of `CalculationResult`.
- `CalculationResult` has 4 fields (exceeds OC #8 of 2 fields), which is accepted for being a result record — document this decision in the cycle log.
