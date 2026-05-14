# SalaryNetCalculator — Ciclo 4

## Descrição

Orquestra o cálculo completo do salário líquido. Recebe um `GrossSalary` validado, delega o cálculo de INSS para `InssTable` e o cálculo de IRRF para `IrrfTable` (usando como base o bruto menos o INSS), e retorna um `CalculationResult` com todos os valores preenchidos.

---

## Checklist de Implementação

### Estrutura da classe
- [x] Criar `src/main/java/com/pfc/tdd/calculator/domain/SalaryNetCalculator.java`
- [x] Construtor com dois parâmetros: `SalaryNetCalculator(InssTable inssTable, IrrfTable irrfTable)`
- [x] Exatamente 2 campos de instância: `inssTable` e `irrfTable` (OC #8)

### Método calculate
- [x] Assinatura: `public CalculationResult calculate(GrossSalary grossSalary)`
- [x] Passo 1: `var inss = inssTable.calculate(grossSalary.value())`
- [x] Passo 2: `var taxableBase = grossSalary.value().subtract(inss)`
- [x] Passo 3: `var irrf = irrfTable.calculate(taxableBase)`
- [x] Passo 4: `var net = grossSalary.value().subtract(inss).subtract(irrf)`
- [x] Passo 5: `return new CalculationResult(grossSalary.value(), inss, irrf, net)`
- [x] Método ≤ 7 linhas (OC #7)

---

## Input

| Parâmetro     | Tipo        | Descrição                                          |
|---------------|-------------|----------------------------------------------------|
| `grossSalary` | GrossSalary | Salário bruto encapsulado e validado (`value > 0`) |

## Output

| Campo   | Tipo       | Descrição                          |
|---------|------------|------------------------------------|
| `gross` | BigDecimal | Salário bruto original             |
| `inss`  | BigDecimal | Contribuição INSS calculada        |
| `irrf`  | BigDecimal | IRRF calculado sobre `bruto−INSS`  |
| `net`   | BigDecimal | Salário líquido (`gross−inss−irrf`)|

---

## Casos de Teste

| Salário Bruto | INSS Esperado | IRRF Esperado | Líquido Esperado | Observação          |
|---------------|---------------|---------------|------------------|---------------------|
| R$ 1.500,00   | R$ 112,50     | R$ 0,00       | R$ 1.387,50      | Isento de IRRF      |
| R$ 5.000,00   | R$ 501,51     | R$ 487,09     | R$ 4.011,40      | Referência principal |

---

## Fluxo de Cálculo

1. `inss = inssTable.calculate(grossSalary.value())`
2. `taxableBase = grossSalary.value() − inss`
3. `irrf = irrfTable.calculate(taxableBase)`
4. `net = grossSalary.value() − inss − irrf`
5. `return CalculationResult(gross, inss, irrf, net)`

> **Atenção:** a base do IRRF é sempre `bruto − INSS`, nunca o bruto direto.

---

## Regras

- O IRRF é calculado sobre `bruto − INSS` — nunca sobre o bruto diretamente.
- Exatamente 2 campos de instância (`inssTable`, `irrfTable`).
- Método `calculate()` com no máximo 7 linhas.
- Sem mocks em testes — usar instâncias reais de `InssTable` e `IrrfTable`.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #5 | Nomes descritivos: `taxableBase`, `inssTable`, `irrfTable` |
| OC #7 | `calculate()` ≤ 7 linhas |
| OC #8 | Exatamente 2 campos de instância |

---

## Suposições

- `InssTable` e `IrrfTable` são stateless e podem ser compartilhadas sem risco.
- `GrossSalary` já foi validado pelo `InputParser` antes de chegar aqui.
