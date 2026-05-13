# TuiFormatter — Ciclo TDD 6

## Descrição

Formata um `CalculationResult` como um painel TUI com bordas Unicode e cores ANSI. É a única classe responsável pela formatação monetária e pela composição visual do painel. Retorna uma `String` multi-linha pronta para ser impressa no terminal.

---

## Checklist de Implementação

### TestHelper (criar primeiro)
- [x] Criar `src/test/java/com/pfc/tdd/calculator/tui/TestHelper.java`
- [x] Método estático: `static String stripAnsi(String text)`
- [x] Implementação: `text.replaceAll("\\033\\[[^m]*m", "")`
- [x] Usar `TestHelper.stripAnsi()` em **todos** os asserts de texto para ignorar ANSI

### TuiFormatter
- [x] Criar `src/test/java/com/pfc/tdd/calculator/tui/TuiFormatterTest.java` (RED)
- [x] Criar `src/main/java/com/pfc/tdd/calculator/tui/TuiFormatter.java` (GREEN)
- [x] Classe stateless — **sem campos de instância**
- [x] Método público: `String format(CalculationResult result)`
- [x] Método privado: `String formatCurrency(BigDecimal value)` para evitar repetição (DRY)

---

## Layout do Painel

```
╭──────────────────────────────────────╮
│  Resumo do Salário                   │   ← texto em ciano
├──────────────────────────────────────┤
│  Salário Bruto          R$ 5.000,00  │
├──────────────────────────────────────┤
│  (-) INSS                 R$ 509,60  │   ← texto em vermelho
│  (-) IRRF                 R$ 479,00  │   ← texto em vermelho
├══════════════════════════════════════╡
│  Salário Líquido        R$ 4.011,40  │   ← texto em verde negrito
╰──────────────────────────────────────╯
```

### Bordas Unicode

| Posição           | Caractere |
|-------------------|-----------|
| Canto sup. esq.   | `╭`       |
| Canto sup. dir.   | `╮`       |
| Canto inf. esq.   | `╰`       |
| Canto inf. dir.   | `╯`       |
| Vertical          | `│`       |
| Horizontal        | `─`       |
| Separador simples | `├ … ┤`   |
| Separador duplo   | `├ ═ … ╡` |

---

## Formatação Monetária

| Valor BigDecimal | Resultado formatado |
|-----------------|---------------------|
| `5000.00`       | `R$ 5.000,00`       |
| `509.60`        | `R$ 509,60`         |
| `1500.00`       | `R$ 1.500,00`       |

**Regras:**
- Separador de milhar: `.` (ponto)
- Separador decimal: `,` (vírgula)
- Prefixo: `R$ `
- Escala: sempre 2 casas decimais

---

## Códigos ANSI

| Elemento         | Código ANSI    |
|------------------|----------------|
| Ciano (cabeçalho) | `\033[36m`    |
| Vermelho (descontos) | `\033[31m` |
| Verde negrito (líquido) | `\033[1;32m` |
| Reset            | `\033[0m`      |

---

## Input

| Parâmetro   | Tipo              | Descrição                              |
|-------------|-------------------|----------------------------------------|
| `result` | CalculationResult  | Resultado com gross, inss, irrf, net |

## Output

| Tipo   | Descrição                                                     |
|--------|---------------------------------------------------------------|
| String | Painel multi-linha com bordas Unicode e ANSI, pronto para print |

---

## Casos de Teste

Usar `TestHelper.stripAnsi(panel)` antes dos asserts:

| O painel deve conter (após strip ANSI) |
|----------------------------------------|
| `"Resumo do Salário"`                  |
| `"Salário Bruto"`                      |
| `"R$ 5.000,00"`                        |
| `"(-) INSS"`                           |
| `"R$ 509,60"`                          |
| `"(-) IRRF"`                           |
| `"R$ 479,00"`                          |
| `"Salário Líquido"`                    |
| `"R$ 4.011,40"`                        |

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #7 | `format()` ≤ 15 linhas; extrair linhas do painel em métodos privados se necessário |
| OC #8 | Nenhum campo de instância — stateless puro |

## DRY aplicado

- `formatCurrency(BigDecimal)` é o único lugar onde a conversão de `BigDecimal` para `"R$ X.XXX,XX"` acontece.
- **Nenhuma** outra classe faz formatação monetária.

---

## Suposições

- A largura interna do painel é fixa em 38 caracteres — não configurável (YAGNI).
- A formatação monetária é feita manualmente (ex: `NumberFormat` com `Locale.forLanguageTag("pt-BR")`) para não depender do locale do sistema.
- Sem suporte a valores acima de R$ 99.999,99 nesta versão.
