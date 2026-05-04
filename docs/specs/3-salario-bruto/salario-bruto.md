# SalarioBruto + ResultadoCalculo — Ciclo TDD 3

## Descrição

Define os tipos de valor imutáveis do domínio. `SalarioBruto` encapsula e valida o salário bruto como record Java, impedindo que um valor negativo ou zero circule pelo sistema. `ResultadoCalculo` agrega os quatro campos de uma apuração de salário líquido — sem lógica de cálculo própria.

---

## Checklist de Implementação

### SalarioBruto
- [ ] Criar `src/test/java/com/pfc/tdd/calculadora/dominio/SalarioBrutoTest.java` (RED)
- [ ] Criar `src/main/java/com/pfc/tdd/calculadora/dominio/SalarioBruto.java` como `record` (GREEN)
- [ ] Adicionar construtor canônico com validação: lançar `IllegalArgumentException` se `valor <= 0`
- [ ] **Nunca usar `double`** — campo é `BigDecimal`

### ResultadoCalculo
- [ ] Criar `src/test/java/com/pfc/tdd/calculadora/dominio/ResultadoCalculoTest.java` (RED)
- [ ] Criar `src/main/java/com/pfc/tdd/calculadora/dominio/ResultadoCalculo.java` como `record` (GREEN)
- [ ] Quatro campos: `bruto`, `inss`, `irrf`, `liquido` — todos `BigDecimal`, não nulos

---

## SalarioBruto

### Definição

```java
public record SalarioBruto(BigDecimal valor) {
    public SalarioBruto {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salário bruto deve ser maior que zero");
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

## ResultadoCalculo

### Definição

```java
public record ResultadoCalculo(
    BigDecimal bruto,
    BigDecimal inss,
    BigDecimal irrf,
    BigDecimal liquido
) {}
```

### Campos

| Campo     | Tipo       | Descrição                                   |
|-----------|------------|---------------------------------------------|
| `bruto`   | BigDecimal | Salário bruto original                      |
| `inss`    | BigDecimal | Contribuição INSS calculada                 |
| `irrf`    | BigDecimal | IRRF calculado                              |
| `liquido` | BigDecimal | Salário líquido (`bruto − inss − irrf`)     |

> `ResultadoCalculo` **não calcula** o líquido — recebe os quatro valores já prontos do `SalarioLiquidoCalculator`.

### Casos de Teste

| bruto       | inss      | irrf      | líquido     |
|-------------|-----------|-----------|-------------|
| R$ 5.000,00 | R$ 509,60 | R$ 479,00 | R$ 4.011,40 |

---

## Regras

- `SalarioBruto.valor` deve ser estritamente maior que zero — zero e negativos lançam `IllegalArgumentException`.
- Ambos os records são **imutáveis** por definição — não adicionar setters nem campos mutáveis.
- `ResultadoCalculo` não tem lógica interna — é apenas um agregador de dados.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #3 | `SalarioBruto` encapsula `BigDecimal` com validação; `Inss`/`Irrf` não são encapsulados (YAGNI) |
| OC #7 | Construtor canônico de `SalarioBruto` ≤ 3 linhas |
| OC #8 | `SalarioBruto`: 1 campo. `ResultadoCalculo`: 4 campos — exception justificada (é um DTO de resultado) |

---

## Suposições

- `Inss` e `Irrf` não são encapsulados em wrappers próprios porque não há risco concreto de troca acidental dado o uso tipado do `ResultadoCalculo`.
- `ResultadoCalculo` tem 4 campos (excede OC #8 de 2 campos), o que é aceito por ser um record de resultado — documentar essa decisão no log de ciclo.
