# TuiApp — Ciclo TDD 8

## Descrição

Ponto de entrada da TUI interativa. Orquestra o fluxo completo: exibe banner, solicita salário em loop, trata entradas inválidas com mensagem amigável (sem stack trace) usando `continue`, calcula o resultado e exibe o painel. Integra todos os componentes anteriores. Também modifica `TddApplication.java` para delegação ao `TuiApp`.

---

## Checklist de Implementação

### TuiApp
- [ ] Criar `src/test/java/com/pfc/tdd/calculadora/tui/TuiAppTest.java` (RED)
- [ ] Criar `src/main/java/com/pfc/tdd/calculadora/tui/TuiApp.java` (GREEN)
- [ ] Construtor: `TuiApp(SalarioLiquidoCalculator calculator, InputParser parser)`
- [ ] Exatamente **2 campos**: `calculator` e `parser` (OC #8)
- [ ] Método público: `void executar(Scanner scanner, PrintStream saida)`
- [ ] `executar()` ≤ 20 linhas (OC #7) — extrair `exibirBanner()` e `solicitarSalario()` se necessário

### TddApplication.java (modificar — não reescrever)
- [ ] Adicionar `implements CommandLineRunner` ou `@Bean CommandLineRunner`
- [ ] Uma linha de delegação: criar `TuiApp` e chamar `executar(new Scanner(System.in), System.out)`

---

## Estrutura de TuiApp

### Construtor

```java
public TuiApp(SalarioLiquidoCalculator calculator, InputParser parser)
```

### Campos (OC #8 — exatamente 2)

```java
private final SalarioLiquidoCalculator calculator;
private final InputParser parser;
```

### Método executar

```java
public void executar(Scanner scanner, PrintStream saida) {
    exibirBanner(saida);
    while (true) {
        saida.print(" Digite o salário bruto (R$): ");
        String entrada = scanner.nextLine().trim();
        SalarioBruto salarioBruto;
        try {
            salarioBruto = parser.parsear(entrada);
        } catch (InputInvalidoException e) {
            saida.println(" " + e.getMessage());
            continue;
        }
        ResultadoCalculo resultado = calcularComAnimacao(salarioBruto, saida);
        saida.println(new TuiFormatter().formatar(resultado));
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

1. Exibir banner via `exibirBanner(saida)`
2. Entrar em loop `while (true)`
3. Exibir prompt: `" Digite o salário bruto (R$): "`
4. Ler linha do `Scanner` e fazer `.trim()`
5. Tentar `parser.parsear(entrada)`:
   - `InputInvalidoException` → imprimir `e.getMessage()` → `continue` (OC #2 — sem `else`)
6. Se `saida == System.out` → criar `TuiAnimator("Calculando...", saida)`, `start()`, calcular, `stop()`
7. Caso contrário → calcular diretamente (sem animação — evita flaky em testes)
8. Exibir painel: `saida.println(new TuiFormatter().formatar(resultado))`
9. `break` — encerra após primeiro resultado bem-sucedido

---

## Input (Construtor)

| Parâmetro    | Tipo                      | Descrição                                        |
|--------------|---------------------------|--------------------------------------------------|
| `calculator` | SalarioLiquidoCalculator  | Calculador do salário líquido                   |
| `parser`     | InputParser               | Parser de entrada do usuário                    |

## Input (executar)

| Parâmetro | Tipo        | Descrição                                                 |
|-----------|-------------|-----------------------------------------------------------|
| `scanner` | Scanner     | Leitura da entrada; criado e fechado pelo chamador        |
| `saida`   | PrintStream | Destino do output; `System.out` em produção               |

---

## Casos de Teste

Nos testes, criar `Scanner` sobre `StringReader` ou `ByteArrayInputStream`:

```java
Scanner scanner = new Scanner("3000\n");
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream saida = new PrintStream(baos);
new TuiApp(calculator, parser).executar(scanner, saida);
String output = baos.toString();
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

- **Sem stack trace** ao usuário — capturar `InputInvalidoException` e exibir apenas `e.getMessage()`.
- **Sem `else`** no loop de retry — usar `continue` após erro (OC #2).
- Toda formatação delegada ao `TuiFormatter` — `TuiApp` não formata texto monetário.
- Animação pulada quando `saida != System.out` — sem risco de flaky por `Thread.sleep` nos testes.

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
        var calculator = new SalarioLiquidoCalculator(new TabelaInss(), new TabelaIrrf());
        new TuiApp(calculator, new InputParser()).executar(new Scanner(System.in), System.out);
    }
}
```

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #2 | Loop de retry sem `else` — captura `InputInvalidoException` e faz `continue` |
| OC #5 | Cada resultado intermediário nomeado: `entrada`, `salarioBruto`, `resultado` |
| OC #7 | `executar()` ≤ 20 linhas; extrair `exibirBanner()` e método de animação |
| OC #8 | Exatamente 2 campos: `calculator` + `parser` |
| YAGNI | Sem flag `--help`, sem modo batch, sem argumentos CLI |
| KISS  | Sem Spring IoC para `TuiApp` — instanciado manualmente |
| DRY   | Toda formatação em `TuiFormatter`; toda validação em `InputParser` |

---

## Suposições

- `TuiApp` não é um `@Component` Spring — instanciado manualmente em `TddApplication.run()`.
- O `Scanner` sobre `System.in` é criado em `TddApplication` e não fechado pelo `TuiApp`.
- Após um cálculo bem-sucedido, o loop termina com `break` — não fica aguardando múltiplas consultas.
