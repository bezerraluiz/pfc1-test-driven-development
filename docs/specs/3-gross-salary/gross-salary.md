# GrossSalary + CalculationResult — Ciclo 3

## Descrição

Define dois tipos de valor imutáveis para o domínio. `GrossSalary` encapsula o salário bruto validando que o valor é estritamente maior que zero. `CalculationResult` é um DTO que agrega os quatro valores do resultado (bruto, INSS, IRRF, líquido) sem nenhuma lógica de cálculo — apenas carrega os dados.

---

## Checklist de Implementação

### GrossSalary
- [ ] Criar `src/main/java/com/pfc/tdd/calculator/domain/GrossSalary.java` como `record`
- [ ] Usar compact constructor para validação: `if (value.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException(...)`
- [ ] Mensagem da exceção: `"Gross salary must be greater than zero"`
- [ ] Campo: `BigDecimal value` — **nunca `double`**

### CalculationResult
- [ ] Criar `src/main/java/com/pfc/tdd/calculator/domain/CalculationResult.java` como `record`
- [ ] Quatro campos: `BigDecimal gross, inss, irrf, net`
- [ ] Sem nenhuma lógica de cálculo — apenas agregação de dados

---

## GrossSalary

### Input

| Parâmetro | Tipo       | Constraint  | Descrição                    |
|-----------|------------|-------------|------------------------------|
| `value`   | BigDecimal | `value > 0` | Salário bruto positivo em R$ |

### Output

| Campo   | Tipo       | Descrição                              |
|---------|------------|----------------------------------------|
| `value` | BigDecimal | Salário bruto validado e encapsulado   |

### Erros

| Exceção                  | Quando                     | Mensagem                               |
|--------------------------|----------------------------|----------------------------------------|
| `IllegalArgumentException` | `value <= 0`             | `"Gross salary must be greater than zero"` |

---

## CalculationResult

### Campos

| Campo   | Tipo       | Descrição                                 |
|---------|------------|-------------------------------------------|
| `gross` | BigDecimal | Salário bruto original                    |
| `inss`  | BigDecimal | Contribuição INSS calculada               |
| `irrf`  | BigDecimal | IRRF calculado                            |
| `net`   | BigDecimal | Salário líquido (bruto − INSS − IRRF)     |

---

## Casos de Teste

| Cenário                      | Input              | Resultado Esperado               |
|------------------------------|--------------------|----------------------------------|
| GrossSalary válido           | `5000.00`          | `GrossSalary(5000.00)` criado    |
| GrossSalary com zero         | `0`                | `IllegalArgumentException`       |
| GrossSalary negativo         | `-100`             | `IllegalArgumentException`       |
| CalculationResult com dados  | `{5000, 509.60, 479.00, 4011.40}` | Record com campos acessíveis |

---

## Regras

- `GrossSalary` valida no compact constructor — nunca permite instância inválida.
- `CalculationResult` **não calcula** — recebe valores já calculados.
- Ambos são imutáveis: records Java não têm setters.
- **Nunca usar `double`** — apenas `BigDecimal`.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #3 | `GrossSalary` encapsula `BigDecimal` com validação de domínio |
| OC #7 | Compact constructor com 1 linha de validação |
| OC #8 | `GrossSalary`: 1 campo; `CalculationResult`: 4 campos (exceção justificada para DTO) |

---

## Suposições

- A validação de formato (String → BigDecimal) é responsabilidade do `InputParser` — `GrossSalary` recebe `BigDecimal` já parseado.
- `CalculationResult` não precisa de validação dos campos — os valores chegam de um cálculo interno confiável.
