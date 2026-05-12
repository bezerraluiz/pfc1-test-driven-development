# TuiApp — Ciclo TDD 8

## Descrição

Ponto de entrada da TUI interativa. Orquestra o fluxo completo: exibe banner, solicita salário em loop, trata entradas inválidas com mensagem amigável (sem stack trace) usando `continue`, calcula o resultado e exibe o painel. Integra todos os componentes anteriores. Também modifica `TddApplication.java` para delegação ao `TuiApp`.

---

## Checklist de Implementação

### TuiApp
- [ ] Criar `src/test/java/com/pfc/tdd/calculator/tui/TuiAppTest.java` (RED)
- [ ] Criar `src/main/java/com/pfc/tdd/calculator/tui/TuiApp.java` (GREEN)
- [ ] Construtor: `TuiApp(SalaryNetCalculator calculator, InputParser parser)`
- [ ] Exatamente **2 campos**: `calculator` e `parser` (OC #8)
- [ ] Método público: `void execute(Scanner scanner, PrintStream output)`
- [ ] `execute()` ≤ 20 linhas (OC #7) — extrair `displayBanner()` e `promptForSalary()` se necessário

### TddApplication.java (modificar — não reescrever)
- [ ] Adicionar `implements CommandLineRunner` ou `@Bean CommandLineRunner`
- [ ] Uma linha de delegação: criar `TuiApp` e chamar `execute(new Scanner(System.`in`), System.out)`

---

## Estrutura de TuiApp

### Construtor

```java
public TuiApp(SalaryNetCalculator calculator, InputParser parser)
```

### Campos (OC #8 — exatamente 2)

```java
private final SalaryNetCalculator calculator;
private final InputParser parser;
```

### Método execute

```java
public void execute(Scanner scanner, PrintStream output) {
    displayBanner(output);
    while (true) {
        output.print(" Digite o salário bruto (R$): ");
        String input = scanner.nextLine().trim();
        GrossSalary grossSalary;
        try {
            grossSalary = parser.parse(input);
        } catch (InvalidInputException e) {
            output.println(" " + e.getMessage());
            continue;
        }
        CalculationResult result = calculateWithAnimation(grossSalary, output);
        output.println(new TuiFormatter().format(result));
        break;
    }
}
```

---

## Banner de Boas-Vindas

```
╭──────────────────────────────────────╮
│   Calculadora de Salário Líquido     │
│             2026                     │
╰──────────────────────────────────────╯
```

---

## Fluxo de Execução

1. Exibir banner via `displayBanner(output)`
2. Entrar em loop `while (true)`
3. Exibir prompt: `" Digite o salário bruto (R$): "`
4. Ler linha do `Scanner` e fazer `.trim()`
5. Tentar `parser.parse(input)`:
   - `InvalidInputException` → imprimir `e.getMessage()` → `continue` (OC #2 — sem `else`)
6. Se `output == System.out` → criar `TuiAnimator("Calculando...", output)`, `start()`, calcular, `stop()`
7. Caso contrário → calcular diretamente (sem animação — evita flaky em testes)
8. Exibir painel: `output.println(new TuiFormatter().format(result))`
9. `break` — encerra após primeiro resultado bem-sucedido

---

## Input (Construtor)

| Parâmetro    | Tipo                      | Descrição                                        |
|--------------|---------------------------|--------------------------------------------------|
| `calculator` | SalaryNetCalculator  | Calculador do salário líquido                   |
| `parser`     | InputParser               | Parser de entrada do usuário                    |

## Input (execute)

| Parâmetro | Tipo        | Descrição                                                 |
|-----------|-------------|-----------------------------------------------------------|
| `scanner` | Scanner     | Leitura da entrada; criado e fechado pelo chamador        |
| `output`   | PrintStream | Destino do output; `System.out` em produção               |

---

## Casos de Teste

Nos testes, criar `Scanner` sobre `StringReader` ou `ByteArrayInputStream`:

```java
Scanner scanner = new Scanner("3000\n");
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream output = new PrintStream(baos);
new TuiApp(calculator, parser).execute(scanner, output);
String result = baos.toString();
```

| # | Entrada simulada     | Output deve conter                            | Output não deve conter |
|---|----------------------|-----------------------------------------------|------------------------|
| 1 | `"3000\n"`           | `"Salário Líquido"`                           | —                      |
| 2 | `"abc\n3000\n"`      | mensagem de erro + `"Salário Líquido"`        | —                      |
| 3 | `"-100\n3000\n"`     | `"maior que zero"` + `"Salário Líquido"`      | —                      |
| 4 | `"0\n3000\n"`        | `"maior que zero"` + `"Salário Líquido"`      | —                      |
| 5 | `"3000\n"`           | —                                             | `"Exception"`, `"at com."` |

---

## Regras

- **Sem stack trace** ao usuário — capturar `InvalidInputException` e exibir apenas `e.getMessage()`.
- **Sem `else`** no loop de retry — usar `continue` após erro (OC #2).
- Toda formatação delegada ao `TuiFormatter` — `TuiApp` não formata texto monetário.
- Animação pulada quando `output != System.out` — sem risco de flaky por `Thread.sleep` nos testes.

---

## Modificação em TddApplication.java

Adicionar uma linha de delegação (não reescrever o arquivo):

```java
@SpringBootApplication
public class TddApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TddApplication.class, args);
    }

    @Override
    public void run(String... args) {
        var calculator = new SalaryNetCalculator(new InssTable(), new IrrfTable());
        new TuiApp(calculator, new InputParser()).execute(new Scanner(System.in), System.out);
    }
}
```

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #2 | Loop de retry sem `else` — captura `InvalidInputException` e faz `continue` |
| OC #5 | Cada resultado intermediário nomeado: `input`, `grossSalary`, `result` |
| OC #7 | `execute()` ≤ 20 linhas; extrair `displayBanner()` e método de animação |
| OC #8 | Exatamente 2 campos: `calculator` + `parser` |
| YAGNI | Sem flag `--help`, sem modo batch, sem argumentos CLI |
| KISS  | Sem Spring IoC para `TuiApp` — instanciado manualmente |
| DRY   | Toda formatação em `TuiFormatter`; toda validação em `InputParser` |

---

## Suposições

- `TuiApp` não é um `@Component` Spring — instanciado manualmente em `TddApplication.run()`.
- O `Scanner` sobre `System.in` é criado em `TddApplication` e não fechado pelo `TuiApp`.
- Após um cálculo bem-sucedido, o loop termina com `break` — não fica aguardando múltiplas consultas.
