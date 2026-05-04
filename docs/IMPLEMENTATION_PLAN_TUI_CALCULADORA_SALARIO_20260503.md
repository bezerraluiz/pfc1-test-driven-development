
**Etapa GREEN**

Criar `src/main/java/com/pfc/tdd/calculadora/tui/TuiAnimator.java`.

Estrutura mínima (KISS):
- 2 campos de instância: `String mensagem` e `PrintStream saida` (OC #8).
- Thread interna com `Runnable` que itera frames `⠋ ⠙ ⠹ ⠸ ⠼ ⠴ ⠦ ⠧ ⠇ ⠏` usando `\r`.
- `stop()` interrompe a thread e limpa a linha com `\033[2K\r`.

**Etapa REFACTOR**

**Princípios aplicados:**
- **OC #8:** exatamente 2 campos de instância (`mensagem` + `saida`). A thread e o `AtomicBoolean` são locais ao método `start()`, não campos.
- **KISS:** `Thread` direta em vez de `ExecutorService`. `AtomicBoolean rodando` para controle de parada seguro.
- **YAGNI:** não tornar os frames configuráveis via construtor — nenhum teste pede isso.
- **Less Code:** `TuiAnimator` ≤ 30 linhas.

---

### Ciclo TDD 8 — `TuiApp` (integração)

**Etapa RED**

Criar `TuiAppTest.java`. `TuiApp` **ainda não existe**.

**Arquivo de teste a criar:** `src/test/java/com/pfc/tdd/calculadora/tui/TuiAppTest.java`

**Estratégia:** Injetar `Scanner` e `PrintStream` via construtor para isolar I/O real dos testes (KISS — sem mock framework).

**Casos de teste:**

| # | Input simulado | Resultado esperado |
|---|---------------|-------------------|
| 1 | `"3000\n"` | Output contém `"Salário Líquido"` e valor calculado |
| 2 | `"abc\n3000\n"` | Output contém mensagem de erro amigável, depois o resultado |
| 3 | `"-100\n3000\n"` | Output contém mensagem de erro, depois o resultado |
| 4 | `"0\n3000\n"` | Output contém mensagem de erro de salário inválido |
| 5 | `"3000\n"` | Output **não** contém stack trace Java |

**Etapa GREEN**

Criar `src/main/java/com/pfc/tdd/calculadora/tui/TuiApp.java` com método `executar(Scanner scanner, PrintStream saida)`:
1. Exibir banner de boas-vindas.
2. Solicitar salário bruto via prompt.
3. Parsear com `InputParser`.
4. Em caso de erro: exibir mensagem amigável e repetir (loop) — sem `else`, usando `continue` (OC #2).
5. Calcular com `SalarioLiquidoCalculator`.
6. Animar com `TuiAnimator` durante cálculo (skip se `saida` não for `System.out`).
7. Formatar e exibir com `TuiFormatter`.

**Etapa REFACTOR**

**Princípios aplicados:**
- **OC #2:** loop de retry sem `else` — capturar `InputInvalidoException`, imprimir erro, `continue`.
- **OC #5:** nomear cada resultado intermediário antes de usá-lo.
- **OC #7:** `TuiApp.executar()` ≤ 20 linhas. Se crescer, extrair `exibirBanner()` e `solicitarSalario()` como métodos privados.
- **DRY:** `TuiApp` não reimplementa formatação — delega inteiramente a `TuiFormatter`.
- **Less Code:** adaptar `TddApplication.java` existente com uma linha de delegação como `CommandLineRunner`. Não reescrever.
- **YAGNI:** não implementar modo batch, flag `--help` ou suporte a argumentos CLI — fora do escopo.

---

## Aparência esperada da TUI

```
╭──────────────────────────────────────╮
│   Calculadora de Salário Líquido     │
│             2026                     │
╰──────────────────────────────────────╯

 Digite o salário bruto (R$): 5000

 ⠸ Calculando...

╭──────────────────────────────────────╮
│  Resumo do Salário                   │
├──────────────────────────────────────┤
│  Salário Bruto          R$ 5.000,00  │
├──────────────────────────────────────┤
│  (-) INSS                 R$ 509,60  │
│  (-) IRRF                 R$ 479,00  │
├══════════════════════════════════════╡
│  Salário Líquido        R$ 4.011,40  │
╰──────────────────────────────────────╯
```

> Cabeçalho: ciano. Descontos: vermelho. Salário Líquido: verde negrito.

---

## Template de documentação por ciclo TDD

A cada ciclo concluído, adicionar uma entrada em `docs/TDD_CYCLES_LOG.md` com:

```markdown
## Ciclo N — NomeClasse

### RED
- **Arquivo de teste criado:** `caminho/do/Teste.java`
- **Casos de teste criados:** lista de cenários
- **Resultado:** ❌ Falha esperada — [motivo: classe não existe / compilação falha]

### GREEN
- **Arquivo(s) de implementação criado(s):** `caminho/da/Classe.java`
- **Resultado:** ✅ Todos os testes passam

### REFACTOR
- **Mudanças:** descrição das mudanças estruturais
- **Princípios aplicados:** lista dos princípios (OC #N, DRY, KISS, etc.)
- **Resultado:** ✅ Testes continuam passando após refactor
```

---

## Riscos técnicos e pontos de atenção

| Risco | Etapa | Mitigação |
|-------|-------|-----------|
| Arredondamento de `double` em valores monetários | Ciclos 1–4 | Usar `BigDecimal` com `RoundingMode.HALF_UP` desde o início — nenhum `double` em cálculos |
| Testes de `TuiAnimator` com `Thread.sleep` → flaky | Ciclo 7 | Usar timeout generoso (200ms) e `AtomicBoolean` para controle limpo |
| ANSI codes quebrando asserts de String | Ciclo 6 | `TestHelper.stripAnsi()` usando regex `\033\[[^m]*m` — um único helper (DRY) |
| Spring Boot tentando subir web server | Etapa 0 | `spring.main.web-application-type=none` em `application.properties` |
| Divergência de package (`br.edu.pfc1` vs `com.pfc.tdd`) | Todos | Adotar `com.pfc.tdd.calculadora` — consistente com `pom.xml` |
| Valores fiscais imprecisos | Ciclos 1–2 | Documentar fonte (Portaria MPS 1.432/2026) e incluir casos-limite (teto, faixas exatas) |
| Violar OC #7 em `TuiFormatter` | Ciclo 6 | Monitorar tamanho no REFACTOR; extrair `PainelSalario` se necessário |
| Overengineering por OC #3 (wrapper em tudo) | Ciclos 3–4 | Aplicar wrappers somente onde trazem segurança de tipo real — `SalarioBruto` sim, `Inss`/`Irrf` somente se houver risco de troca acidental |

---

## Definição de Pronto

O plano está implementado quando:

1. `./mvnw test` passa com 0 falhas.
2. `./mvnw spring-boot:run` inicia a TUI interativa.
3. Cada um dos 8 ciclos TDD está documentado em `docs/TDD_CYCLES_LOG.md` com RED/GREEN/REFACTOR.
4. Os cálculos de salário R$ 1.500, R$ 3.000 e R$ 5.000 produzem resultados corretos verificados manualmente.
5. Entradas inválidas (texto, negativo, zero) são tratadas sem stack trace visível ao usuário.
6. **Object Calisthenics:** cada classe passa na checklist das 9 regras (verificar com revisão manual no REFACTOR de cada ciclo).
7. **DRY:** nenhum valor de faixa fiscal duplicado fora de `TabelaInss`/`TabelaIrrf`. Formatação de moeda em um único lugar.
8. **YAGNI:** nenhuma classe, método ou campo sem teste que o justifique.
9. **KISS:** nenhum método com mais de um nível de indentação ou mais de 5 linhas no corpo.
10. **Less Code:** nenhum boilerplate manual onde um `record` resolve.
