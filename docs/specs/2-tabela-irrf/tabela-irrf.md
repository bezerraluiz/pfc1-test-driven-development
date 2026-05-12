# TabelaIrrf — Ciclo TDD 2

## Descrição

Classe stateless que calcula o IRRF (Imposto de Renda Retido na Fonte) a partir da base de cálculo — que é o salário bruto menos a contribuição do INSS. O cálculo usa a tabela progressiva mensal: identifica a faixa em que a base se enquadra, aplica a alíquota sobre a base inteira e subtrai a parcela dedutível correspondente.

---

## Checklist de Implementação

### Estrutura da classe
- [ ] Criar `src/test/java/com/pfc/tdd/calculadora/dominio/TabelaIrrfTest.java` (RED)
- [ ] Criar `src/main/java/com/pfc/tdd/calculadora/dominio/TabelaIrrf.java` (GREEN)
- [ ] Nenhum campo de instância — tabela como constante estática
- [ ] Método público único: `BigDecimal calcular(BigDecimal baseCalculo)`
- [ ] Usar `BigDecimal` em todos os cálculos — **nunca `double`**

### Algoritmo
- [ ] Encontrar a faixa em que `baseCalculo` se enquadra
- [ ] Se `baseCalculo ≤ R$ 2.428,80` → retornar `BigDecimal.ZERO`
- [ ] Caso contrário: `irrf = baseCalculo × aliquota − parcelaDedutivel`
- [ ] Aplicar `max(irrf, ZERO)` para garantir resultado não-negativo
- [ ] Arredondar com `setScale(2, RoundingMode.HALF_UP)` no resultado final

---

## Tabela IRRF 05/2025

> **ATENÇÃO:** Os valores abaixo foram atualizados conforme a tabela IRRF 05/2025 fornecida como referência.

| Faixa | Base de Cálculo                     | Alíquota | Parcela Dedutível |
|-------|--------------------------------------|----------|-------------------|
| 1     | Até R$ 2.428,80                     | Isento   | —                 |
| 2     | R$ 2.428,81 a R$ 2.826,65          | 7,5%     | R$ 182,16         |
| 3     | R$ 2.826,66 a R$ 3.751,05          | 15%      | R$ 394,16         |
| 4     | R$ 3.751,06 a R$ 4.664,68          | 22,5%    | R$ 675,49         |
| 5     | Acima de R$ 4.664,68               | 27,5%    | R$ 908,73         |

---

## Input

| Parâmetro      | Tipo       | Descrição                                                             |
|----------------|------------|-----------------------------------------------------------------------|
| `baseCalculo`  | BigDecimal | Salário bruto menos INSS — nunca negativo; calculado antes pelo chamador |

## Output

| Campo  | Tipo       | Escala | Arredondamento | Mínimo | Descrição                    |
|--------|------------|--------|----------------|--------|------------------------------|
| `irrf` | BigDecimal | 2      | HALF_UP        | 0.00   | IRRF apurado; nunca negativo |

---

## Casos de Teste

| Base de Cálculo | IRRF Esperado   | Observação                                              |
|-----------------|-----------------|---------------------------------------------------------|
| R$ 1.500,00     | R$ 0,00         | Abaixo da isenção                                       |
| R$ 2.428,80     | R$ 0,00         | No limite exato da isenção                              |
| R$ 3.500,00     | R$ 130,84       | Faixa 3: 3500 × 15% − 394,16 = 525,00 − 394,16         |
| R$ 4.490,40     | R$ 334,85       | Faixa 4: 4490,40 × 22,5% − 675,49 = 1010,34 − 675,49   |

---

## Fluxo de Cálculo (exemplo R$ 3.500,00 — faixa 3)

1. `baseCalculo = 3.500,00`
2. Enquadra na faixa 3 (2.826,66 a 3.751,05)
3. `IRRF = 3.500,00 × 15% − 394,16 = 525,00 − 394,16 = 130,84`
4. Resultado: **R$ 130,84**

---

## Regras

- A base de cálculo é sempre `grossSalary − INSS` — esta classe não recebe o salário bruto nem o INSS separadamente.
- Resultado nunca negativo: se `base × aliquota − deducao < 0`, retornar `BigDecimal.ZERO`.
- Nenhuma dedução por dependentes — fora do escopo (YAGNI).
- **Sem arredondamento intermediário** — arredondar apenas o resultado final.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #7 | `calcular()` ≤ 5 linhas; lógica de faixa em método privado se necessário |
| OC #8 | Nenhum campo de instância — tabela como constante estática |

---

## Suposições

- Os valores da tabela foram alinhados à referência visual `Tabela IRRF 05/2025` enviada.
- Sem desconto simplificado ou dependentes — implementação mínima conforme YAGNI.
