# InputParser + InvalidInputException — Ciclo TDD 5

## Descrição

Converte a string digitada pelo usuário em um `GrossSalary`. Lança `InvalidInputException` (unchecked) para entradas não numéricas, zero ou negativas, com mensagens em português que a TUI exibe diretamente ao usuário — sem stack trace visível.

---

## Checklist de Implementação

### InvalidInputException
- [x] Criar `src/main/java/com/pfc/tdd/calculator/tui/InvalidInputException.java`
- [x] Estender `RuntimeException`
- [x] Construtor único: `InvalidInputException(String mensagem)`

### InputParser
- [x] Criar `src/test/java/com/pfc/tdd/calculator/tui/InputParserTest.java` (RED)
- [x] Criar `src/main/java/com/pfc/tdd/calculator/tui/InputParser.java` (GREEN)
- [x] Classe stateless — **sem campos de instância**
- [x] Método público: `GrossSalary parse(String text)`

### Algoritmo do método parse
- [x] Se `text` for nulo, vazio ou em branco → lançar `InvalidInputException`
- [x] Substituir vírgula por ponto: `text.replace(",", ".")`
- [x] Tentar `new BigDecimal(text)` — capturar `NumberFormatException` → lançar `InvalidInputException` com mensagem de entrada inválida
- [x] Tentar criar `new GrossSalary(valor)` — capturar `IllegalArgumentException` → lançar `InvalidInputException` com mensagem de salário inválido
- [x] Retornar o `GrossSalary` criado
- [x] **Sem `else`** — usar early throw (OC #2)

---

## Input

| Parâmetro | Tipo   | Descrição                                          |
|-----------|--------|----------------------------------------------------|
| `text`    | String | String lida do Scanner; quem chama deve fazer trim |

## Output

| Tipo          | Descrição                                                      |
|---------------|----------------------------------------------------------------|
| `GrossSalary` | Record com o valor parseado, sempre positivo                 |

## Erros

| Exceção                  | Trigger                                    | Mensagem ao usuário                                          |
|--------------------------|--------------------------------------------|--------------------------------------------------------------|
| `InvalidInputException` | Texto não numérico ou vazio               | `"Entrada inválida. Digite um número válido, como 3000 ou 3000,50."` |
| `InvalidInputException` | Valor zero ou negativo (após parse)       | `"O salário deve ser maior que zero."`                       |

---

## Casos de Teste

| Entrada      | Resultado esperado                                    |
|--------------|-------------------------------------------------------|
| `"3000"`     | `GrossSalary` com valor `3000.00`                    |
| `"3000.50"`  | `GrossSalary` com valor `3000.50`                    |
| `"3000,50"`  | `GrossSalary` com valor `3000.50` (vírgula aceita)   |
| `"abc"`      | `InvalidInputException` — entrada inválida           |
| `"0"`        | `InvalidInputException` — salário deve ser > zero    |
| `"-100"`     | `InvalidInputException` — salário deve ser > zero    |
| `""`         | `InvalidInputException` — entrada inválida           |

---

## Regras

- **Aceitar vírgula** como separador decimal: `"3000,50"` é válido.
- **Sem stack trace** exposto ao usuário — a mensagem da exception é toda a informação que a TUI precisa.
- `InvalidInputException` é **unchecked** — a TUI captura explicitamente, sem `throws` obrigatório.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #2 | `parse()` usa early throw em vez de if-else aninhado |
| OC #7 | `parse()` ≤ 8 linhas |
| OC #8 | Nenhum campo de instância |

---

## Suposições

- O `trim()` da string é feito pela TUI antes de chamar `parse()` — `InputParser` não faz trim.
- `InvalidInputException` é unchecked para simplificar o loop de retry em `TuiApp` (sem `throws` declarado no método).
