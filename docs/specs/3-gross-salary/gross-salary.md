# GrossSalary + CalculationResult — Ciclo TDD 3

## Descrição

Define os tipos de valor imutáveis do domínio. `GrossSalary` encapsula e valida o salário bruto como record Java, impedindo que um valor negativo ou zero circule pelo sistema. `CalculationResult` agrega os quatro campos de uma apuração de salário líquido — sem lógica de cálculo própria.

---

## Checklist de Implementação

### GrossSalary
- [x] Criar `src/test/java/com/pfc/tdd/calculator/domain/GrossSalaryTest.java` (RED)
- [x] Criar `src/main/java/com/pfc/tdd/calculator/domain/GrossSalary.java` como `record` (GREEN)
- [x] Adicionar construtor canônico com validação: lançar `IllegalArgumentException` se `value <= 0`
- [x] **Nunca usar `double`** — campo é `BigDecimal`

### CalculationResult
- [x] Criar `src/test/java/com/pfc/tdd/calculator/domain/CalculationResultTest.java` (RED)
- [x] Criar `src/main/java/com/pfc/tdd/calculator/domain/CalculationResult.java` como `record` (GREEN)
- [x] Quatro campos: `gross`, `inss`, `irrf`, `net` — todos `BigDecimal`, não nulos

---

## GrossSalary

### Definição

```java
public record GrossSalary(BigDecimal value) {
    public GrossSalary {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Gross salary must be greater than zero");
        }
    }
}
```

### Casos de Teste

| Entrada       | Resultado esperado                          |
|---------------|---------------------------------------------|
| `3000.00`     | Record criado com sucesso                   |
| `0.01`        | Record criado com sucesso (mínimo válido)   |
| `0.00`        | `IllegalArgumentException`                  |
| `-100.00`     | `IllegalArgumentException`                  |
| `null`        | `NullPointerException` (comportamento padrão do record) |

---

## CalculationResult

### Definição

```java
public record CalculationResult(
    BigDecimal gross,
    BigDecimal inss,
    BigDecimal irrf,
    BigDecimal net
) {}
```

### Campos

| Campo     | Tipo       | Descrição                                   |
|-----------|------------|---------------------------------------------|
| `gross`   | BigDecimal | Salário bruto original                      |
| `inss`    | BigDecimal | Contribuição INSS calculada                 |
| `irrf`    | BigDecimal | IRRF calculado                              |
| `net`     | BigDecimal | Salário líquido (`gross − inss − irrf`)     |

> `CalculationResult` **não calcula** o líquido — recebe os quatro valores já prontos do `SalaryNetCalculator`.

### Casos de Teste

| gross       | inss      | irrf      | net         |
|-------------|-----------|-----------|-------------|
| R$ 5.000,00 | R$ 509,60 | R$ 479,00 | R$ 4.011,40 |

---

## Regras

- `GrossSalary.value` deve ser estritamente maior que zero — zero e negativos lançam `IllegalArgumentException`.
- Ambos os records são **imutáveis** por definição — não adicionar setters nem campos mutáveis.
- `CalculationResult` não tem lógica interna — é apenas um agregador de dados.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #3 | `GrossSalary` encapsula `BigDecimal` com validação; `inss`/`irrf` não são encapsulados (YAGNI) |
| OC #7 | Construtor canônico de `GrossSalary` ≤ 3 linhas |
| OC #8 | `GrossSalary`: 1 campo. `CalculationResult`: 4 campos — exception justificada (é um DTO de resultado) |

---

## Suposições

- `inss` e `irrf` não são encapsulados em wrappers próprios porque não há risco concreto de troca acidental dado o uso tipado do `CalculationResult`.
- `CalculationResult` tem 4 campos (excede OC #8 de 2 campos), o que é aceito por ser um record de resultado — documentar essa decisão no log de ciclo.
