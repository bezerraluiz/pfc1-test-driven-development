# InputParser + InputInvalidoException — Ciclo TDD 5

## Descrição

Converte a string digitada pelo usuário em um `SalarioBruto`. Lança `InputInvalidoException` (unchecked) para entradas não numéricas, zero ou negativas, com mensagens em português que a TUI exibe diretamente ao usuário — sem stack trace visível.

---

## Checklist de Implementação

### InputInvalidoException
- [ ] Criar `src/main/java/com/pfc/tdd/calculadora/tui/InputInvalidoException.java`
- [ ] Estender `RuntimeException`
- [ ] Construtor único: `InputInvalidoException(String mensagem)`

### InputParser
- [ ] Criar `src/test/java/com/pfc/tdd/calculadora/tui/InputParserTest.java` (RED)
- [ ] Criar `src/main/java/com/pfc/tdd/calculadora/tui/InputParser.java` (GREEN)
- [ ] Classe stateless — **sem campos de instância**
- [ ] Método público: `SalarioBruto parsear(String texto)`

### Algoritmo do método parsear
- [ ] Se `texto` for nulo, vazio ou em branco → lançar `InputInvalidoException`
- [ ] Substituir vírgula por ponto: `texto.replace(",", ".")`
- [ ] Tentar `new BigDecimal(texto)` — capturar `NumberFormatException` → lançar `InputInvalidoException` com mensagem de entrada inválida
- [ ] Tentar criar `new SalarioBruto(valor)` — capturar `IllegalArgumentException` → lançar `InputInvalidoException` com mensagem de salário inválido
- [ ] Retornar o `SalarioBruto` criado
- [ ] **Sem `else`** — usar early throw (OC #2)

---

## Input

| Parâmetro | Tipo   | Descrição                                          |
|-----------|--------|----------------------------------------------------|
| `texto`   | String | String lida do Scanner; quem chama deve fazer trim |

## Output

| Tipo          | Descrição                                                      |
|---------------|----------------------------------------------------------------|
| `SalarioBruto` | Record com o valor parseado, sempre positivo                 |

## Erros

| Exceção                  | Trigger                                    | Mensagem ao usuário                                          |
|--------------------------|--------------------------------------------|--------------------------------------------------------------|
| `InputInvalidoException` | Texto não numérico ou vazio               | `"Entrada inválida. Digite um número válido, como 3000 ou 3000,50."` |
| `InputInvalidoException` | Valor zero ou negativo (após parse)       | `"O salário deve ser maior que zero."`                       |

---

## Casos de Teste

| Entrada      | Resultado esperado                                    |
|--------------|-------------------------------------------------------|
| `"3000"`     | `SalarioBruto` com valor `3000.00`                    |
| `"3000.50"`  | `SalarioBruto` com valor `3000.50`                    |
| `"3000,50"`  | `SalarioBruto` com valor `3000.50` (vírgula aceita)   |
| `"abc"`      | `InputInvalidoException` — entrada inválida           |
| `"0"`        | `InputInvalidoException` — salário deve ser > zero    |
| `"-100"`     | `InputInvalidoException` — salário deve ser > zero    |
| `""`         | `InputInvalidoException` — entrada inválida           |

---

## Regras

- **Aceitar vírgula** como separador decimal: `"3000,50"` é válido.
- **Sem stack trace** exposto ao usuário — a mensagem da exception é toda a informação que a TUI precisa.
- `InputInvalidoException` é **unchecked** — a TUI captura explicitamente, sem `throws` obrigatório.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #2 | `parsear()` usa early throw em vez de if-else aninhado |
| OC #7 | `parsear()` ≤ 8 linhas |
| OC #8 | Nenhum campo de instância |

---

## Suposições

- O `trim()` da string é feito pela TUI antes de chamar `parsear()` — `InputParser` não faz trim.
- `InputInvalidoException` é unchecked para simplificar o loop de retry em `TuiApp` (sem `throws` declarado no método).
