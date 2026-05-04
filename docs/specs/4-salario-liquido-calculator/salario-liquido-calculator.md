# SalarioLiquidoCalculator — Ciclo TDD 4

## Descrição

Orquestra o cálculo completo do salário líquido. Recebe um `SalarioBruto`, delega o cálculo do INSS para `TabelaInss` e do IRRF para `TabelaIrrf`, e retorna um `ResultadoCalculo` com os quatro valores. É o único ponto de entrada da lógica de negócio.

---

## Checklist de Implementação

### Estrutura da classe
- [ ] Criar `src/test/java/com/pfc/tdd/calculadora/dominio/SalarioLiquidoCalculatorTest.java` (RED)
- [ ] Criar `src/main/java/com/pfc/tdd/calculadora/dominio/SalarioLiquidoCalculator.java` (GREEN)
- [ ] Construtor: `SalarioLiquidoCalculator(TabelaInss tabelaInss, TabelaIrrf tabelaIrrf)`
- [ ] Exatamente **2 campos de instância**: `tabelaInss` e `tabelaIrrf` (OC #8)
- [ ] Nos testes: instanciar `TabelaInss` e `TabelaIrrf` reais — **sem mock**

### Método calcular
- [ ] Assinatura: `ResultadoCalculo calcular(SalarioBruto salarioBruto)`
- [ ] Nomear cada resultado intermediário (OC #5):
  - `BigDecimal inss = tabelaInss.calcular(salarioBruto.valor())`
  - `BigDecimal baseIrrf = salarioBruto.valor().subtract(inss)`
  - `BigDecimal irrf = tabelaIrrf.calcular(baseIrrf)`
  - `BigDecimal liquido = salarioBruto.valor().subtract(inss).subtract(irrf)`
- [ ] Retornar `new ResultadoCalculo(salarioBruto.valor(), inss, irrf, liquido)`
- [ ] Método ≤ 7 linhas (OC #7)

---

## Input

| Parâmetro       | Tipo          | Descrição                                      |
|-----------------|---------------|------------------------------------------------|
| `salarioBruto`  | SalarioBruto  | Wrapper de valor já validado — nunca nulo      |

## Output

| Campo     | Tipo       | Origem                               |
|-----------|------------|--------------------------------------|
| `bruto`   | BigDecimal | `salarioBruto.valor()`               |
| `inss`    | BigDecimal | `TabelaInss.calcular(bruto)`         |
| `irrf`    | BigDecimal | `TabelaIrrf.calcular(bruto − inss)`  |
| `liquido` | BigDecimal | `bruto − inss − irrf`                |

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

1. `inss = TabelaInss.calcular(5000.00)` → R$ 509,60
2. `baseIrrf = 5000.00 − 509.60` → R$ 4.490,40
3. `irrf = TabelaIrrf.calcular(4490.40)` → R$ 479,00 (confirmar)
4. `liquido = 5000.00 − 509.60 − 479.00` → **R$ 4.011,40**

---

## Regras

- Não revalida o valor do salário — `SalarioBruto` já garante que é positivo.
- Não instancia `TabelaInss` ou `TabelaIrrf` internamente — recebe via construtor.
- Resultados intermediários devem ter nomes descritivos (OC #5).

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #5 | Cada resultado intermediário tem nome: `inss`, `baseIrrf`, `irrf`, `liquido` |
| OC #7 | `calcular()` ≤ 7 linhas |
| OC #8 | Exatamente 2 campos: `tabelaInss` e `tabelaIrrf` |

---

## Suposições

- `TabelaInss` e `TabelaIrrf` são stateless — pode criar uma única instância em testes sem efeitos colaterais.
- Nos testes, usar instâncias reais das tabelas (não mocks) — confirma integração entre os ciclos.
