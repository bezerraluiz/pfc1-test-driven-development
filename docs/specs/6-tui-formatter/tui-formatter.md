# TuiFormatter — Ciclo 6

## Descrição

Formata um `CalculationResult` como painel de terminal com bordas Unicode, cores ANSI e formatação monetária brasileira. Produz uma string multi-linha pronta para `output.println()`. É stateless — sem campos de instância.

---

## Checklist de Implementação

### Estrutura
- [ ] Criar `src/main/java/com/pfc/tdd/calculator/tui/TuiFormatter.java`
- [ ] Classe stateless — sem campos de instância (OC #8)
- [ ] Método público: `public String format(CalculationResult result)`
- [ ] Método privado: `private String formatCurrency(BigDecimal value)` — única fonte de formatação monetária (DRY)

### Formatação monetária
- [ ] Separador de milhar: `.` (ponto)
- [ ] Separador decimal: `,` (vírgula)
- [ ] Prefixo: `R$ `
- [ ] Sempre 2 casas decimais
- [ ] Exemplo: `BigDecimal("5000.40")` → `"R$ 5.000,40"`

### Layout do painel
- [ ] Borda superior: `╭──────────────────────────────────────╮`
- [ ] Cabeçalho "Resumo do Salário" em ciano: `\033[36m` + reset `\033[0m`
- [ ] Separador: `├──────────────────────────────────────┤`
- [ ] Linha "Salário Bruto" com valor alinhado à direita
- [ ] Separador após bruto
- [ ] Linha "(-) INSS" em vermelho: `\033[31m` + reset
- [ ] Linha "(-) IRRF" em vermelho: `\033[31m` + reset
- [ ] Separador duplo: `├══════════════════════════════════════╡`
- [ ] Linha "Salário Líquido" em verde negrito: `\033[1;32m` + reset
- [ ] Borda inferior: `╰──────────────────────────────────────╯`

### Auxiliar para testes
- [ ] Criar `src/test/java/com/pfc/tdd/calculator/tui/TestHelper.java`
- [ ] Método: `static String stripAnsi(String text)` → `text.replaceAll("\\033\\[[^m]*m", "")`

---

## Input

| Parâmetro | Tipo              | Descrição                        |
|-----------|-------------------|----------------------------------|
| `result`  | CalculationResult | Resultado com gross/inss/irrf/net |

## Output

| Campo   | Tipo   | Descrição                                     |
|---------|--------|-----------------------------------------------|
| `panel` | String | Painel multi-linha com bordas e cores ANSI    |

---

## Layout Esperado

```
╭──────────────────────────────────────╮
│  Resumo do Salário                   │
├──────────────────────────────────────┤
│  Salário Bruto          R$ 5.000,00  │
├──────────────────────────────────────┤
│  (-) INSS                 R$ 501,51  │
│  (-) IRRF                 R$ 487,09  │
├══════════════════════════════════════╡
│  Salário Líquido        R$ 4.011,40  │
╰──────────────────────────────────────╯
```

---

## Regras

- `formatCurrency` é o **único** lugar com lógica de formatação monetária — não duplicar (DRY).
- Largura interna fixa: 38 caracteres.
- Códigos de cor ANSI: ciano `\033[36m`, vermelho `\033[31m`, verde negrito `\033[1;32m`, reset `\033[0m`.
- Usar `TestHelper.stripAnsi()` nos testes para remover ANSI antes de fazer assertions.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #7 | `format()` extrai linhas em métodos privados; `formatCurrency()` isolado |
| OC #8 | Nenhum campo de instância |
| DRY   | `formatCurrency()` é a única fonte de formatação monetária |

---

## Suposições

- Terminal com suporte a UTF-8 e ANSI — sem suporte, bordas e cores não renderizam.
- Valores acima de R$ 99.999,99 podem desalinhar o painel (não suportado).
