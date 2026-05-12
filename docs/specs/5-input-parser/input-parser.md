# InputParser + InvalidInputException — Ciclo TDD 5

## Descrição

Converte a string digitada pelo usuário em um `GrossSalary`. Lança `InvalidInputException` (unchecked) para entradas não numéricas, zero ou negativas, com mensagens em português que a TUI exibe diretamente ao usuário — sem stack trace visível.

---

## Checklist de Implementação

### InvalidInputException
- [ ] Criar `src/main/java/com/pfc/tdd/calculator/tui/InvalidInputException.java`
- [ ] Estender `RuntimeException`
- [ ] Construtor único: `InvalidInputException(String mensagem)`

### InputParser
- [ ] Criar `src/test/java/com/pfc/tdd/calculator/tui/InputParserTest.java` (RED)
- [ ] Criar `src/main/java/com/pfc/tdd/calculator/tui/InputParser.java` (GREEN)
- [ ] Classe stateless — **sem campos de instância**
- [ ] Método público: `GrossSalary parse(String text)`

### Algoritmo do método parse
- [ ] Se `text` for nulo, vazio ou em branco → lançar `InvalidInputException`
- [ ] Substituir vírgula por ponto: `text.replace(",", ".")`
- [ ] Tentar `new BigDecimal(text)` — capturar `NumberFormatException` → lançar `InvalidInputException` com mensagem de entrada inválida
- [ ] Tentar criar `new GrossSalary(valor)` — capturar `IllegalArgumentException` → lançar `InvalidInputException` com mensagem de salário inválido
- [ ] Retornar o `GrossSalary` criado
- [ ] **Sem `else`** — usar early throw (OC #2)

---

## Input

| Parâmetro | Tipo   | Descrição                                          |
|-----------|--------|----------------------------------------------------|
| `text`   | String | String lida do Scanner; quem chama deve fazer trim |

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
| OC #7 | `parse()` ≤ 8 lines |
| OC #8 | Nenhum campo de instância |

---

## Suposições

- O `trim()` da string é feito pela TUI antes de chamar `parsear()` — `InputParser` não faz trim.
- `InputInvalidoException` é unchecked para simplificar o loop de retry em `TuiApp` (sem `throws` declarado no método).
