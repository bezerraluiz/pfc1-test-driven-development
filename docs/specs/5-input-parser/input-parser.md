# InputParser + InvalidInputException — Ciclo 5

## Descrição

`InputParser` converte a string digitada pelo usuário em um `GrossSalary` válido. Aceita vírgula como separador decimal. Lança `InvalidInputException` (unchecked) com mensagens em português para entradas inválidas ou valores fora do domínio. `InvalidInputException` é uma exceção simples que estende `RuntimeException`.

---

## Checklist de Implementação

### InvalidInputException
- [ ] Criar `src/main/java/com/pfc/tdd/calculator/tui/InvalidInputException.java`
- [ ] Estender `RuntimeException` (unchecked)
- [ ] Construtor: `public InvalidInputException(String mensagem)`

### InputParser
- [ ] Criar `src/main/java/com/pfc/tdd/calculator/tui/InputParser.java`
- [ ] Classe stateless — sem campos de instância (OC #8)
- [ ] Método público: `public GrossSalary parse(String text)`

### Algoritmo de parse (sem else — OC #2)
- [ ] Se `text == null` ou `text.isBlank()` → lançar `InvalidInputException` com mensagem de entrada inválida
- [ ] Substituir `,` por `.` para aceitar vírgula decimal: `text.replace(",", ".")`
- [ ] Tentar `new BigDecimal(text)` — se lançar `NumberFormatException` → lançar `InvalidInputException` com mensagem de entrada inválida
- [ ] Tentar `new GrossSalary(value)` — se lançar `IllegalArgumentException` → lançar `InvalidInputException` com mensagem de salário inválido
- [ ] Retornar `GrossSalary`

---

## Input

| Parâmetro | Tipo   | Aceita | Descrição                             |
|-----------|--------|--------|---------------------------------------|
| `text`    | String | nulo, vazio, decimal com `,` ou `.` | Texto digitado pelo usuário |

## Output

| Campo        | Tipo        | Descrição                         |
|--------------|-------------|-----------------------------------|
| `grossSalary` | GrossSalary | Salário bruto validado            |

## Erros

| Código           | Exceção               | Quando                             | Mensagem                                                      |
|------------------|-----------------------|------------------------------------|---------------------------------------------------------------|
| INVALID_INPUT    | InvalidInputException | Nulo, vazio, branco ou não numérico | `"Entrada inválida. Digite um número válido, como 3000 ou 3000,50."` |
| INVALID_SALARY   | InvalidInputException | Valor zero ou negativo             | `"O salário deve ser maior que zero."`                        |

---

## Casos de Teste

| Entrada     | Resultado Esperado                        |
|-------------|-------------------------------------------|
| `"3000"`    | `GrossSalary(3000.00)` ✓                  |
| `"3000.50"` | `GrossSalary(3000.50)` ✓                  |
| `"3000,50"` | `GrossSalary(3000.50)` ✓ (vírgula aceita) |
| `"abc"`     | `InvalidInputException` (entrada inválida) |
| `"0"`       | `InvalidInputException` (salário inválido) |
| `"-100"`    | `InvalidInputException` (salário inválido) |
| `""`        | `InvalidInputException` (entrada inválida) |

---

## Regras

- **Sem `else`** — usar early throw a cada verificação (OC #2).
- Vírgula é aceita como separador decimal (substituída por ponto antes do parse).
- Mensagens de erro em português — nunca stack trace para o usuário.
- Trim não é responsabilidade desta classe — feito por `TuiApp` antes de chamar `parse()`.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #2 | Early throw sem else — cada verificação encerra imediatamente se falhar |
| OC #7 | `parse()` ≤ 8 linhas |
| OC #8 | Nenhum campo de instância em `InputParser` |

---

## Suposições

- O `trim()` é feito em `TuiApp` antes de chamar `parse()` — `InputParser` não faz trim.
- Entrada com símbolo `R$` não é suportada — somente o número puro.
