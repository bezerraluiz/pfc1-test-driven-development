# Plano de implementação - Calculadora de Salário Líquido (Sem TDD)

> 2026-05-13
> Branch: without-tdd
> Desenvolvedor responsável: Luiz Antônio e Anna Clara

## Resumo sobre a task

- **Objetivo:** Implementar um calculador de salário líquido em Java SEM a prática de Test-Driven Development, para servir de controle comparativo em estudo acadêmico sobre o impacto do TDD na legibilidade e redução de erros.
- **Resultado esperado e critério de aceite:** Aplicação funcional de terminal (TUI) que recebe salário bruto, calcula INSS e IRRF progressivos com as tabelas vigentes em 2026, e exibe o salário líquido formatado com bordas Unicode e cores ANSI.
- **Escopo:**
  - Cálculo progressivo de INSS (Portaria Interministerial MPS/MF nº 13, 2026)
  - Cálculo progressivo de IRRF (tabela 05/2025)
  - Validação e parseamento da entrada do usuário (português)
  - Interface TUI interativa com spinner animado e painel formatado
  - Integração com Spring Boot como ponto de entrada
- **Fora de escopo:**
  - Testes escritos antes do código (TDD) — a ausência é intencional e metodológica
  - Descontos simplificados, dependentes, ou outros descontos além de INSS/IRRF
  - Interface web, API REST ou qualquer canal que não seja terminal
  - Persistência de dados

## Padrões a preservar

- **Arquitetura em camadas:** TUI Layer (`TuiApp`, `TuiFormatter`, `TuiAnimator`) → Domain Layer (`SalaryNetCalculator`, `InssTable`, `IrrfTable`) → Value Objects (`GrossSalary`, `CalculationResult`)
- **Object Calisthenics** aplicados conforme especificações em `docs/specs/`:
  - OC #2: Sem `else` após early throw/return
  - OC #3: Encapsulamento de primitivos em Value Objects
  - OC #5: Nomes descritivos sem abreviações
  - OC #7: Métodos com no máximo 5–20 linhas por classe (variável por ciclo)
  - OC #8: No máximo 2 campos de instância por classe
- **Imutabilidade:** `GrossSalary` e `CalculationResult` como `record`
- **BigDecimal** para todos os cálculos monetários (nunca `double`)
- **Sem arredondamento intermediário:** arredondar apenas o total final (`setScale(2, RoundingMode.HALF_EVEN)` para INSS, `HALF_UP` para IRRF)
- **Mensagens de erro em português** em `InputParser`
- **Injeção de `PrintStream`** em `TuiAnimator` e `TuiApp` para testabilidade futura
- **Spring Boot `CommandLineRunner`** em `TddApplication` como único ponto de entrada
- **Convenção de pacotes:** `com.pfc.tdd.calculator.domain`, `com.pfc.tdd.calculator.tui`

## Arquivos existentes que se relacionam com a task

| Arquivo | Relação com a task |
|---|---|
| `src/main/java/com/pfc/tdd/TddApplication.java` | Ponto de entrada Spring Boot; delega ao `TuiApp` via `CommandLineRunner` — **fato observado** |
| `src/test/java/com/pfc/tdd/TddApplicationTests.java` | Smoke test de contexto Spring; único teste existente — **fato observado** |
| `docs/specs/1-tabela-inss/tabela-inss.md` + `.yaml` | Contrato da `InssTable`: tabela INSS 2026, algoritmo progressivo, 5 casos de teste — **fato observado** |
| `docs/specs/2-tabela-irrf/tabela-irrf.md` + `.yaml` | Contrato da `IrrfTable`: tabela IRRF 05/2025, parcelas dedutíveis, 4 casos de teste — **fato observado** |
| `docs/specs/3-gross-salary/gross-salary.md` + `.yaml` | Contrato de `GrossSalary` e `CalculationResult` como records imutáveis — **fato observado** |
| `docs/specs/4-salario-liquido-calculator/salario-liquido-calculator.md` + `.yaml` | Contrato de `SalaryNetCalculator`: fluxo de orquestração em ≤ 7 linhas — **fato observado** |
| `docs/specs/5-input-parser/input-parser.md` + `.yaml` | Contrato de `InputParser` e `InvalidInputException`: validação, vírgula como separador decimal — **fato observado** |
| `docs/specs/6-tui-formatter/tui-formatter.md` + `.yaml` | Contrato de `TuiFormatter`: bordas Unicode, cores ANSI, formatação monetária brasileira — **fato observado** |
| `docs/specs/7-tui-animator/tui-animator.md` + `.yaml` | Contrato de `TuiAnimator`: frames Braille, thread interrompível, limpeza de linha — **fato observado** |
| `docs/specs/8-tui-app/tui-app.md` + `.yaml` | Contrato de `TuiApp`: loop interativo, banner, delegação com/sem animação — **fato observado** |
| `pom.xml` | Spring Boot 4.0.6, Java 21, JUnit 5 via `spring-boot-starter-test` — **fato observado** |

> **Nota metodológica:** Na branch `without-tdd`, o código é escrito primeiro; os testes unitários por ciclo são ausentes intencionalmente. O único teste existente (`contextLoads()`) é o mínimo exigido pelo Spring Boot.

## Dependências externas

- **Java 21** — linguagem principal; `record` e inferência de tipo (`var`) utilizados
- **Spring Boot 4.0.6** (`spring-boot-starter`) — bootstrap da aplicação e `CommandLineRunner`
- **JUnit 5** (`spring-boot-starter-test`) — disponível para smoke test e testes futuros de comparação
- **Nenhuma dependência de terceiros além das acima** — sem Lombok, sem Guava, sem Jackson; tudo com stdlib Java
- **Pré-condição:** JDK 21 instalado e `JAVA_HOME` configurado; Maven Wrapper (`mvnw`) disponível no repositório

## Ordem recomendada de implementação

> Esta ordem reflete a sequência já adotada na branch `without-tdd` (sem TDD), do menor risco ao maior, respeitando dependências entre classes.

### Etapa 1 — Domínio financeiro (base de tudo)

1. **`InssTable`** (`com.pfc.tdd.calculator.domain`)
   - Constante estática com as 4 faixas INSS 2026
   - Método `calculate(BigDecimal grossSalary)` com loop progressivo
   - Arredondamento final: `setScale(2, HALF_EVEN)`
   - Risco: lógica de faixa progressiva pode ser confundida com alíquota única; revisar tabela na spec

2. **`IrrfTable`** (`com.pfc.tdd.calculator.domain`)
   - Constante estática com as 5 faixas IRRF 05/2025 e parcelas dedutíveis
   - Método `calculate(BigDecimal taxableBase)`
   - Arredondamento: `setScale(2, HALF_UP)`
   - Risco: garantir `max(irrf, ZERO)` para não retornar negativo

### Etapa 2 — Value Objects

3. **`GrossSalary`** (record) — validação `> 0`, lança `IllegalArgumentException`
4. **`CalculationResult`** (record) — DTO agregador sem lógica de cálculo

### Etapa 3 — Orquestrador de cálculo

5. **`SalaryNetCalculator`**
   - Recebe `InssTable` e `IrrfTable` no construtor (2 campos — OC #8)
   - Método `calculate(GrossSalary)` com 5 passos: inss → base tributável → irrf → net → retornar DTO
   - Risco: ordem de cálculo importa; base do IRRF é `bruto - INSS`, não o bruto

### Etapa 4 — Camada de entrada

6. **`InvalidInputException`** (unchecked, extends `RuntimeException`)
7. **`InputParser`**
   - Aceitar vírgula como separador decimal (substituir `,` por `.`)
   - Early throw sem `else` (OC #2)
   - Mensagens de erro em português, conforme spec

### Etapa 5 — Camada de apresentação TUI

8. **`TuiFormatter`**
   - Método privado `formatCurrency(BigDecimal)` → `"R$ X.XXX,XX"` (DRY)
   - Caracteres Unicode e códigos ANSI conforme layout da spec
   - Ponto de atenção: ANSI pode não renderizar em todos os terminais; testar localmente

9. **`TuiAnimator`**
   - Thread separada com frames Braille, sleep 80ms
   - `stop()` usa `interrupt()` + `join(200)` + limpeza de linha ANSI
   - Injetar `PrintStream` (nunca `System.out` diretamente)
   - Risco: race condition em `stop()` se `start()` não for chamado antes; garantir guards

10. **`TuiApp`**
    - Loop interativo: banner → prompt → parse → calcular → exibir → break
    - Sem animação quando `output != System.out` (facilita testes futuros)
    - Métodos privados: `displayBanner()`, `calculateWithAnimation()`
    - Risco: integração entre todas as camadas; testar fluxo manualmente antes de commitar

### Etapa 6 — Integração Spring Boot

11. **`TddApplication`**
    - Implementar `CommandLineRunner`
    - Instanciar `SalaryNetCalculator`, `InputParser`, `TuiApp` na mão (sem DI automático)
    - `SpringApplication.run()` no `main()`
    - Validar com `./mvnw spring-boot:run`

---

> **Risco geral da abordagem sem TDD:** Sem testes guiando o design, desvios de contrato (arredondamento, mensagens, layout) só aparecem em execução manual. Recomenda-se executar o fluxo completo no terminal após cada etapa e comparar visualmente com os casos de teste das specs em `docs/specs/`.
