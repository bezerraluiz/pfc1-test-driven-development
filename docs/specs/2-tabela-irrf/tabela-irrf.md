# IrrfTable — Ciclo 2

## Descrição

Classe stateless responsável por calcular o IRRF (Imposto de Renda Retido na Fonte) a partir da base de cálculo tributável, que é o salário bruto menos o INSS já calculado. Usa a tabela progressiva mensal de maio/2025. O resultado nunca é negativo; para bases na faixa de isenção, retorna R$ 0,00.

---

## Checklist de Implementação

### Estrutura da classe
- [x] Criar `src/main/java/com/pfc/tdd/calculator/domain/IrrfTable.java`
- [x] Definir a tabela de faixas como constante estática com alíquota e parcela dedutível
- [x] Expor um único método público: `BigDecimal calculate(BigDecimal taxableBase)`
- [x] Usar `BigDecimal` em todo cálculo — **nunca `double`**

### Algoritmo de cálculo
- [x] Se `taxableBase <= R$ 2.428,80` → retornar `BigDecimal.ZERO`
- [x] Para as demais faixas: `irrf = taxableBase × aliquota − parcelaDedutivel`
- [x] Aplicar `max(irrf, ZERO)` para garantir resultado não-negativo
- [x] Aplicar `setScale(2, RoundingMode.HALF_UP)` ao total final

---

## Tabela IRRF 05/2025

| Faixa | Limite Superior | Alíquota | Parcela Dedutível |
|-------|-----------------|----------|-------------------|
| 1     | R$ 2.428,80     | Isento   | —                 |
| 2     | R$ 2.826,65     | 7,5%     | R$ 182,16         |
| 3     | R$ 3.751,05     | 15%      | R$ 394,16         |
| 4     | R$ 4.664,68     | 22,5%    | R$ 675,49         |
| 5     | Acima de tudo   | 27,5%    | R$ 908,73         |

> Fonte: Tabela IRRF 05/2025

---

## Input

| Parâmetro     | Tipo       | Descrição                                            |
|---------------|------------|------------------------------------------------------|
| `taxableBase` | BigDecimal | Base tributável (salário bruto − INSS); deve ser positivo |

## Output

| Campo  | Tipo       | Escala | Arredondamento | Mínimo | Descrição            |
|--------|------------|--------|----------------|--------|----------------------|
| `irrf` | BigDecimal | 2      | HALF_UP        | 0,00   | Valor do IRRF apurado |

---

## Casos de Teste

| Base de Cálculo | IRRF Esperado | Observação                       |
|-----------------|---------------|----------------------------------|
| R$ 1.500,00     | R$ 0,00       | Abaixo da isenção                |
| R$ 2.428,80     | R$ 0,00       | Limite exato da isenção          |
| R$ 3.500,00     | R$ 130,84     | Faixa 3: 3500 × 15% − 394,16   |
| R$ 4.490,40     | R$ 334,85     | Faixa 4: 4490,40 × 22,5% − 675,49 |

---

## Regras

- A base de cálculo é sempre `bruto − INSS` — esta classe não faz esse desconto, recebe já calculado.
- Faixa de isenção: base ≤ R$ 2.428,80 → retornar `BigDecimal.ZERO` diretamente.
- Resultado nunca negativo: aplicar `max(irrf, ZERO)` antes do arredondamento.
- Arredondamento `HALF_UP`, escala 2, aplicado apenas ao total final.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #7 | `calculate()` ≤ 5 linhas; extrair helper se necessário |
| OC #8 | Nenhum campo de instância — tabela como constante estática |

---

## Suposições

- A tabela IRRF de 05/2025 é a vigente; atualizações futuras exigem alteração direta em `IrrfTable`.
- A base de cálculo já vem com o INSS descontado — esta classe não realiza esse desconto.
