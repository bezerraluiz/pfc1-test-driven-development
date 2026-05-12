# TabelaInss — Ciclo TDD 1

## Descrição

Classe stateless responsável por calcular a contribuição do INSS com base no salário bruto usando a tabela progressiva da Portaria Interministerial MPS/MF nº 13, de 9 de janeiro de 2026. O cálculo é feito por faixas: cada faixa tem sua alíquota própria aplicada somente sobre o valor do salário dentro daquela faixa, e as parcelas são somadas ao final.

---

## Checklist de Implementação

### Estrutura da classe
- [x] Criar `src/main/java/com/pfc/tdd/calculator/domain/InssTable.java`
- [x] Definir a tabela de faixas como constante estática (`private static final List<...>` ou array)
- [x] Expor um único método público: `BigDecimal calculate(BigDecimal grossSalary)`
- [x] Usar `BigDecimal` em todo cálculo — **nunca `double`**

### Algoritmo de cálculo
- [x] Para cada faixa da tabela, calcular a parcela de INSS: `min(salario, limiteSuperior) - limiteInferior` multiplicado pela `aliquota`
- [x] Acumular as parcelas sem arredondar cada uma individualmente
- [x] Aplicar `setScale(2, RoundingMode.HALF_EVEN)` **apenas** no total final
- [x] Para salário acima do teto (R$ 8.475,55), calcular somente até o teto

### Testes a escrever (RED first)
- [x] `InssTableTest.java` em `src/test/java/com/pfc/tdd/calculator/domain/`
- [x] Cobrir cada caso da tabela abaixo

---

## Tabela INSS 2026 (Portaria Interministerial MPS/MF nº 13, de 9 de janeiro de 2026)

| Faixa | Limite Superior | Alíquota |
|-------|-----------------|----------|
| 1     | R$ 1.621,00     | 7,5%     |
| 2     | R$ 2.902,84     | 9%       |
| 3     | R$ 4.354,27     | 12%      |
| 4     | R$ 8.475,55     | 14%      |

> **Atenção:** Confirmar os valores exatos na publicação oficial da Portaria Interministerial MPS/MF nº 13, de 9 de janeiro de 2026 antes de finalizar a implementação.

---

## Input

| Parâmetro      | Tipo       | Descrição                                                      |
|----------------|------------|----------------------------------------------------------------|
| `grossSalary` | BigDecimal | Salário bruto positivo, já validado pelo wrapper `GrossSalary` |

## Output

| Campo  | Tipo       | Escala | Arredondamento | Descrição                         |
|--------|------------|--------|----------------|-----------------------------------|
| `inss` | BigDecimal | 2      | HALF_EVEN      | Contribuição total do INSS apurada |

---

## Casos de Teste

| Salário Bruto  | INSS Esperado | Observação                          |
|----------------|--------------|--------------------------------------|
| R$ 1.500,00    | R$ 112,50    | Apenas faixa 1 (1500 × 7,5%)        |
| R$ 1.621,00    | R$ 121,58    | Teto exato da faixa 1               |
| R$ 3.000,00    | R$ 248,60    | Faixas 1 + 2 + parcial 3            |
| R$ 5.000,00    | R$ 501,51    | Faixas 1 + 2 + 3 + parcial 4        |
| R$ 8.475,55    | R$ 988,09    | Teto máximo — faixas 1+2+3+4 completas |

---

## Fluxo de Cálculo (exemplo R$ 5.000,00)

1. Faixa 1: `1.621,00 × 7,5%` = R$ 121,575
2. Faixa 2: `(2.902,84 − 1.621,00) × 9%` = R$ 115,3656
3. Faixa 3: `(4.354,27 − 2.902,84) × 12%` = R$ 174,1716
4. Faixa 4: `(5.000,00 − 4.354,27) × 14%` = R$ 90,4022
5. Soma bruta: R$ 501,5144
6. Arredondado HALF_EVEN → **R$ 501,51**

---

## Regras

- O cálculo é **progressivo**: não aplica uma única alíquota sobre o salário total.
- **Sem arredondamento intermediário** por faixa — arredondar apenas o total final.
- Salário acima do teto (`R$ 8.475,55`): calcular somente até o teto; o excedente não gera INSS adicional.
- Validação de valor negativo/zero: **não é responsabilidade desta classe** — delegado ao `GrossSalary`.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #3 | Receber `BigDecimal` diretamente (GrossSalary já valida antes) |
| OC #7 | `calculate()` ≤ 5 linhas; extrair `calculateInstallment()` se necessário |
| OC #8 | Nenhum campo de instância — tabela como constante estática |

---

## Suposições

- Os valores da tabela INSS 2026 devem seguir a tabela progressiva documentada nesta especificação — confirmar na publicação oficial.
- Salário abaixo de R$ 0,01 não chegará a esta classe (validado antes por `GrossSalary`).
