# SalaryNetCalculator — Ciclo TDD 4

## Descrição

Orquestra o cálculo completo do salário líquido. Recebe um `GrossSalary`, delega o cálculo do INSS para `InssTable` e do IRRF para `IrrfTable`, e retorna um `CalculationResult` com os quatro valores. É o único ponto de entrada da lógica de negócio.

---

## Checklist de Implementação

### Estrutura da classe
- [x] Criar `src/test/java/com/pfc/tdd/calculator/domain/SalaryNetCalculatorTest.java` (RED)
- [x] Criar `src/main/java/com/pfc/tdd/calculator/domain/SalaryNetCalculator.java` (GREEN)
- [x] Construtor: `SalaryNetCalculator(InssTable inssTable, IrrfTable irrfTable)`
- [x] Exatamente **2 campos de instância**: `inssTable` e `irrfTable` (OC #8)
- [x] Nos testes: instanciar `InssTable` e `IrrfTable` reais — **sem mock**

### Método calculate
- [x] Assinatura: `CalculationResult calculate(GrossSalary grossSalary)`
- [x] Nomear cada resultado intermediário (OC #5):
  - `BigDecimal inss = inssTable.calculate(grossSalary.value())`
  - `BigDecimal taxableBase = grossSalary.value().subtract(inss)`
  - `BigDecimal irrf = irrfTable.calculate(taxableBase)`
  - `BigDecimal net = grossSalary.value().subtract(inss).subtract(irrf)`
- [x] Retornar `new CalculationResult(grossSalary.value(), inss, irrf, net)`
- [x] Método ≤ 7 linhas (OC #7)

---

## Input

| Parâmetro       | Tipo          | Descrição                                      |
|-----------------|---------------|------------------------------------------------|
| `grossSalary`  | GrossSalary  | Wrapper de valor já validado — nunca nulo      |

## Output

| Campo     | Tipo       | Origem                               |
|-----------|------------|--------------------------------------|
| `gross`   | BigDecimal | `grossSalary.value()`               |
| `inss`    | BigDecimal | `InssTable.calculate(gross)`         |
| `irrf`    | BigDecimal | `IrrfTable.calculate(gross − inss)`  |
| `net`     | BigDecimal | `gross − inss − irrf`                |

---

## Casos de Teste

| Salário Bruto   | INSS Esperado | IRRF Esperado | Líquido Esperado |
|-----------------|--------------|--------------|-----------------|
| R$ 1.500,00     | R$ 112,50    | R$ 0,00      | R$ 1.387,50     |
| R$ 3.000,00     | R$ 253,41    | a confirmar  | a confirmar     |
| R$ 5.000,00     | R$ 509,60    | R$ 479,00    | R$ 4.011,40     |

> Confirmar os valores de IRRF com a tabela oficial de 2026 antes de escrever os testes.

---

## Fluxo de Cálculo (R$ 5.000,00)

1. `inss = InssTable.calculate(5000.00)` → R$ 509,60
2. `taxableBase = 5000.00 − 509.60` → R$ 4.490,40
3. `irrf = IrrfTable.calculate(4490.40)` → R$ 479,00 (confirmar)
4. `net = 5000.00 − 509.60 − 479.00` → **R$ 4.011,40**

---

## Regras

- Não revalida o valor do salário — `GrossSalary` já garante que é positivo.
- Não instancia `InssTable` ou `IrrfTable` internamente — recebe via construtor.
- Resultados intermediários devem ter nomes descritivos (OC #5).

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #5 | Cada resultado intermediário tem nome: `inss`, `taxableBase`, `irrf`, `net` |
| OC #7 | `calculate()` ≤ 7 linhas |
| OC #8 | Exatamente 2 campos: `inssTable` e `irrfTable` |

---

## Suposições

- `InssTable` e `IrrfTable` são stateless — pode criar uma única instância em testes sem efeitos colaterais.
- Nos testes, usar instâncias reais das tabelas (não mocks) — confirma integração entre os ciclos.
