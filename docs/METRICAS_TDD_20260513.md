# Métricas do Desenvolvimento com TDD

**Projeto:** Calculadora de Salário Líquido  
**Branch:** `with-tdd`  
**Stack:** Java 21 · Spring Boot 4.0.6 · JUnit 5 · Maven  
**Período:** 25/02/2026 – 13/05/2026

---

## 1. Resumo Executivo

O projeto foi desenvolvido integralmente sob a metodologia TDD (Test-Driven Development), seguindo o ciclo **RED → GREEN → REFACTOR** para cada componente. Foram realizados **8 ciclos de desenvolvimento** — do núcleo de cálculo à interface de linha de comando — totalizando **38 testes** distribuídos em **10 classes de teste**.

---

## 2. Ciclos TDD

Cada ciclo partiu de um documento de especificação (`.md` + `.yaml`) criado antes da implementação.

| # | Componente | Testes | Tipo |
|---|---|---|---|
| 1 | `InssTable` | 5 | Unitário |
| 2 | `IrrfTable` | 4 | Unitário |
| 3 | `GrossSalary` + `CalculationResult` | 6 | Unitário |
| 4 | `SalaryNetCalculator` | 2 | Integração (domínio) |
| 5 | `InputParser` | 6 | Unitário |
| 6 | `TuiFormatter` | 9 | Unitário |
| 7 | `TuiAnimator` | 3 | Unitário |
| 8 | `TuiApp` | 5 | Integração (E2E) |
| — | `TddApplicationTests` | 1 | Spring Context |
| — | `TestHelper` | — | Utilitário (sem testes próprios) |

---

## 3. Cobertura de Testes

### 3.1 Totais

| Métrica | Valor |
|---|---|
| Classes de teste | 10 |
| Métodos de teste | 38 |
| Classes de produção | 11 |
| Testes unitários | 33 |
| Testes de integração | 5 |

### 3.2 Por camada

| Camada | Classes produção | Classes de teste | Métodos de teste |
|---|---|---|---|
| Domain | 5 | 5 | 19 |
| TUI | 5 | 4 | 18 |
| Bootstrap | 1 | 1 | 1 |
| **Total** | **11** | **10** | **38** |

### 3.3 Distribuição dos tipos de verificação

| Tipo de verificação | Quantidade |
|---|---|
| Happy path (fluxo esperado) | 21 |
| Edge case (limite/fronteira) | 7 |
| Error/exceção | 10 |

---

## 4. Histograma de Commits

```
Fev 2026  ░▓░ (1 commit — commit inicial)
Mar 2026  ░░░
Abr 2026  ░▓░ (1 commit — README inicial)
Mai 01    ░░░
Mai 02    ▓░░ feat: projeto Spring Boot inicializado
Mai 04    ▓░░ docs: especificações criadas (8 ciclos)
Mai 06    ▓░░ chore: configuração IDE
Mai 07    ▓░░ feat: InssTable (ciclo 1)
Mai 09    ▓░░ feat: IrrfTable (ciclo 2)
Mai 12    ▓▓▓▓▓ feat: GrossSalary, SalaryNetCalculator,
               InputParser, TuiFormatter, TuiAnimator (ciclos 3–7)
Mai 13    ▓░░ feat: TuiApp + integração final (ciclo 8)
```

### Commits por categoria

| Categoria | Quantidade |
|---|---|
| `feat` (implementações) | 9 |
| `docs` (documentação/specs) | 4 |
| `fix` | 1 |
| `chore` | 1 |
| **Total** | **15** |

---

## 5. Detalhamento dos Testes por Componente

### 5.1 `GrossSalaryTest` — 5 testes

| Teste | Cenário |
|---|---|
| `shouldCreateGrossSalaryWithValidValue` | Happy path — valor válido (3000,00) |
| `shouldCreateGrossSalaryWithMinimumValidValue` | Edge case — valor mínimo (0,01) |
| `shouldThrowIllegalArgumentExceptionForZeroValue` | Erro — valor zero |
| `shouldThrowIllegalArgumentExceptionForNegativeValue` | Erro — valor negativo |
| `shouldThrowNullPointerExceptionForNullValue` | Erro — null |

### 5.2 `InssTableTest` — 5 testes

| Teste | Entrada | Resultado Esperado |
|---|---|---|
| Primeira faixa | R$ 1.500,00 | R$ 112,50 |
| Limite da 1ª faixa | R$ 1.621,00 | R$ 121,58 |
| Cruzando 3ª faixa | R$ 3.000,00 | R$ 248,60 |
| Cruzando 4ª faixa | R$ 5.000,00 | R$ 501,51 |
| Teto do INSS | R$ 8.475,55 | R$ 988,09 |

### 5.3 `IrrfTableTest` — 4 testes

| Teste | Entrada | Resultado Esperado |
|---|---|---|
| Isento | R$ 1.500,00 | R$ 0,00 |
| 2ª faixa (7,5%) | R$ 2.490,40 | R$ 4,62 |
| 4ª faixa (22,5%) | R$ 4.490,40 | R$ 334,85 |

### 5.4 `SalaryNetCalculatorTest` — 2 testes (integração)

| Bruto | INSS | IRRF | Líquido |
|---|---|---|---|
| R$ 3.000,00 | R$ 248,60 | R$ 24,20 | R$ 2.727,20 |
| R$ 5.000,00 | R$ 501,51 | R$ 336,67 | R$ 4.161,82 |

### 5.5 `InputParserTest` — 6 testes

| Teste | Entrada | Resultado |
|---|---|---|
| Inteiro | `"3000"` | 3000,00 |
| Ponto como separador | `"3000.50"` | 3000,50 |
| Vírgula como separador | `"3000,50"` | 3000,50 |
| Texto inválido | `"abc"` | `InvalidInputException` |
| Zero | `"0"` | `InvalidInputException` |
| Vazio | `""` | `InvalidInputException` |

### 5.6 `TuiFormatterTest` — 9 testes

Verifica a presença de cada campo no painel formatado (cabeçalho, rótulos e valores em pt-BR).

| Campo verificado | Exemplo de saída esperada |
|---|---|
| Cabeçalho | `"Resumo do Salário"` |
| Salário Bruto | `"R$ 5.000,00"` |
| INSS | `"R$ 509,60"` |
| IRRF | `"R$ 479,00"` |
| Salário Líquido | `"R$ 4.011,40"` |

### 5.7 `TuiAnimatorTest` — 3 testes

| Teste | Verificação |
|---|---|
| `shouldDisplayMessageWhileRunning` | Animação exibe "Calculando..." enquanto ativa |
| `shouldClearLineOnStop` | Código ANSI `\033[2K` presente após stop |
| `shouldNotGenerateOutputAfterStop` | Nenhuma saída adicional gerada após encerramento |

### 5.8 `TuiAppTest` — 5 testes (E2E)

| Teste | Entrada simulada | Verificação |
|---|---|---|
| Fluxo normal | `"3000\n"` | Contém "Salário Líquido" |
| Entrada inválida → retry | `"abc\n3000\n"` | Mensagem de erro + resultado |
| Negativo → retry | `"-100\n3000\n"` | Mensagem de erro + resultado |
| Zero → retry | `"0\n3000\n"` | Mensagem de erro + resultado |
| Sem stack trace | `"abc\n3000\n"` | Ausência de `Exception` ou stack trace na saída |

---

## 6. Indicadores de Qualidade do Código

### 6.1 Princípios aplicados

| Princípio | Evidência |
|---|---|
| **Object Calisthenics** (9 regras) | Documentado no plano de implementação |
| **Imutabilidade** | `GrossSalary` e `CalculationResult` implementados como Java Records |
| **Separação de responsabilidades** | Camadas `domain` e `tui` estritamente separadas |
| **Injeção de dependência** | `SalaryNetCalculator`, `TuiApp` recebem dependências via construtor |
| **Precisão financeira** | `BigDecimal` com arredondamento `HALF_EVEN` em todos os cálculos |
| **Testabilidade** | `Scanner`/`PrintStream` injetados em `TuiApp` para testes sem mocks |
| **Localização** | Mensagens de erro e formatação monetária em pt-BR |

### 6.2 Razão teste / código de produção

| Métrica | Valor |
|---|---|
| Classes de produção | 11 |
| Classes de teste | 10 |
| Proporção classes teste/produção | ~0,91 |
| Métodos de teste | 38 |
| Ratio aproximado testes por feature | ~3,5 por ciclo |

---

## 7. Cronologia do Desenvolvimento

| Data | Evento |
|---|---|
| 25/02/2026 | Commit inicial do repositório |
| 01/04/2026 | README inicial com objetivo do projeto |
| 02/05/2026 | Projeto Spring Boot inicializado |
| 04/05/2026 | Especificações dos 8 ciclos criadas (`.md` + `.yaml`) |
| 07/05/2026 | Ciclo 1 concluído — `InssTable` |
| 09/05/2026 | Ciclo 2 concluído — `IrrfTable` |
| 12/05/2026 | Ciclos 3–7 concluídos — domínio + camada TUI |
| 13/05/2026 | Ciclo 8 concluído — `TuiApp` e integração final |

**Total de commits:** 15

---

## 8. Conclusões

- O processo TDD produziu **38 testes antes ou em conjunto com cada implementação**, garantindo que o comportamento esperado foi sempre definido explicitamente antes do código existir.
- A separação entre **especificação (docs/specs)**, **teste** e **implementação** resultou em classes com responsabilidade única e baixo acoplamento.
- Os **5 testes de integração E2E** em `TuiAppTest` detectaram precocemente problemas de fluxo (retry de entrada inválida, ausência de stack trace na saída do usuário) sem depender de mocks frágeis.
- A abordagem **documentation-first** (specs `.md`/`.yaml` antes de qualquer código) funcionou como contrato entre os ciclos TDD, reduzindo retrabalho e tornando as revisões de escopo rastreáveis via git.
