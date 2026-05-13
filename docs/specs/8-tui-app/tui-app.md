# TuiApp — Ciclo 8

## Descrição

Orquestra o fluxo completo da TUI interativa. Exibe um banner, solicita o salário em loop com tratamento de erros, exibe o spinner de animação durante o cálculo (apenas no terminal real) e mostra o painel de resultado. Encerra após o primeiro cálculo bem-sucedido. Também cobre a atualização de `TddApplication` para delegar ao `TuiApp` via `CommandLineRunner`.

---

## Checklist de Implementação

### TuiApp
- [ ] Criar `src/main/java/com/pfc/tdd/calculator/tui/TuiApp.java`
- [ ] Construtor: `public TuiApp(SalaryNetCalculator calculator, InputParser parser)`
- [ ] Exatamente 2 campos de instância: `calculator` e `parser` (OC #8)
- [ ] Método público: `public void execute(Scanner scanner, PrintStream output)`
- [ ] `execute()` ≤ 20 linhas (OC #7)

### Método execute()
- [ ] Chamar `displayBanner(output)` antes do loop
- [ ] Loop `while (true)`:
  - [ ] Imprimir prompt: `output.print(" Digite o salário bruto (R$): ")`
  - [ ] Ler: `var input = scanner.nextLine().trim()`
  - [ ] Tentar `parser.parse(input)` — se `InvalidInputException`: `output.println(e.getMessage()); continue`
  - [ ] Se `output == System.out`: chamar `calculateWithAnimation(grossSalary, output)`
  - [ ] Senão: `var result = calculator.calculate(grossSalary)`
  - [ ] `output.println(new TuiFormatter().format(result))`
  - [ ] `break`

### Métodos privados
- [ ] `private void displayBanner(PrintStream output)` — exibir o banner de boas-vindas
- [ ] `private CalculationResult calculateWithAnimation(GrossSalary grossSalary, PrintStream output)` — criar animator, start, calculate, stop

### TddApplication (atualizar)
- [ ] Implementar `CommandLineRunner` em `TddApplication`
- [ ] Método `run()`: instanciar `SalaryNetCalculator`, `InputParser`, `TuiApp` manualmente
- [ ] `SpringApplication.run()` no `main()`

---

## Input (construtor)

| Parâmetro    | Tipo                | Descrição                           |
|--------------|---------------------|-------------------------------------|
| `calculator` | SalaryNetCalculator | Orquestrador de cálculo injetado    |
| `parser`     | InputParser         | Parser de entrada injetado          |

## Input (execute)

| Parâmetro  | Tipo        | Descrição                              |
|------------|-------------|----------------------------------------|
| `scanner`  | Scanner     | Fonte de entrada do usuário            |
| `output`   | PrintStream | Destino de saída (System.out ou teste) |

---

## Banner

```
╭──────────────────────────────────────╮
│   Calculadora de Salário Líquido     │
│             2026                     │
╰──────────────────────────────────────╯
```

---

## Casos de Teste

| Entrada         | Output deve conter                   | Output NÃO deve conter       |
|-----------------|--------------------------------------|------------------------------|
| `"3000\n"`      | `"Salário Líquido"`                  | —                            |
| `"abc\n3000\n"` | mensagem de erro + `"Salário Líquido"` | —                          |
| `"-100\n3000\n"`| `"maior que zero"` + `"Salário Líquido"` | —                        |
| `"3000\n"`      | —                                    | `"Exception"`, `"at com."`   |

---

## Fluxo de Execução

1. Exibir banner
2. Loop:
   - Prompt de entrada
   - Tentar parse → se inválido, mostrar erro e continuar
   - Calcular (com ou sem animação)
   - Exibir painel de resultado
   - Encerrar
3. Aplicação encerra

---

## Regras

- **Sem animação nos testes**: a animação só é exibida quando `output == System.out`.
- **Sem stack trace ao usuário**: capturar `InvalidInputException` e exibir apenas a mensagem.
- Sem `else` após `continue` (OC #2).
- `execute()` ≤ 20 linhas; extrair métodos privados se necessário.

---

## Modificação em TddApplication

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
| OC #2 | `continue` após erro — sem `else` |
| OC #5 | Nomes descritivos: `displayBanner`, `calculateWithAnimation` |
| OC #7 | `execute()` ≤ 20 linhas |
| OC #8 | Exatamente 2 campos: `calculator`, `parser` |
| YAGNI | Sem suporte a múltiplos cálculos por sessão |
| KISS  | Verificação de `output == System.out` para evitar animação em testes |

---

## Suposições

- `Scanner` é criado externamente e passado via `execute()` para permitir testes sem `System.in`.
- `TuiFormatter` não tem estado — instanciar a cada chamada é aceitável.
