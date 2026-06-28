# Métricas do Desenvolvimento sem TDD

**Projeto:** Calculadora de Salário Líquido  
**Branch:** `without-tdd`  
**Contraparte TDD:** branch `with-tdd` (documento `METRICAS_TDD_20260513.md`)  
**Stack:** Java 21 · Spring Boot 4.0.6 · Maven  
**Período de desenvolvimento ativo:** 13–14/05/2026

---

## 1. Resumo Executivo

O projeto foi desenvolvido sem a prática de TDD, servindo de grupo de controle para estudo acadêmico sobre o impacto do Test-Driven Development. O mesmo conjunto de funcionalidades foi implementado nas duas branches — 8 ciclos, do núcleo de cálculo à interface de terminal — com a diferença estrutural de que, aqui, **nenhum teste foi escrito antes ou junto ao código de produção**.

Para compensar a ausência de testes como guia de design, adotou-se um processo de **especificação-primeiro via contratos YAML** (`docs/specs/`), traduzidos manualmente para código pela skill `feature-implementation`. O processo revelou pontos de atrito específicos que não existiam na abordagem TDD.

---

## 2. Comparativo de Cobertura de Testes

| Métrica | `with-tdd` | `without-tdd` | Diferença |
|---|---|---|---|
| Classes de teste | 10 | 1 | −9 (−90%) |
| Métodos de teste | 38 | 1 | −37 (−97,4%) |
| Testes unitários | 33 | 0 | −33 |
| Testes de integração | 5 | 0 | −5 |
| Smoke test de contexto Spring | 1 | 1 | 0 |

O único teste existente na branch `without-tdd` é o `contextLoads()` gerado automaticamente pelo Spring Boot — ele verifica apenas que o contexto da aplicação sobe sem erros, sem validar nenhuma regra de negócio.

---

## 3. Métricas de Código

### 3.1 Linhas de código por arquivo

| Arquivo | `with-tdd` | `without-tdd` | Δ linhas |
|---|---|---|---|
| `InssTable.java` | 36 | 32 | −4 |
| `IrrfTable.java` | 31 | 45 | +14 (+45%) |
| `GrossSalary.java` | 11 | 11 | 0 |
| `CalculationResult.java` | 10 | 6 | −4 |
| `SalaryNetCalculator.java` | 22 | 20 | −2 |
| `InputParser.java` | 18 | 29 | +11 (+61%) |
| `InvalidInputException.java` | 7 | 7 | 0 |
| `TuiFormatter.java` | 51 | 55 | +4 |
| `TuiAnimator.java` | 37 | 44 | +7 (+19%) |
| `TuiApp.java` | 55 | 56 | +1 |
| `TddApplication.java` | 22 | 25 | +3 |
| **Total produção** | **300** | **330** | **+30 (+10%)** |
| **Total testes** | **473** | **13** | **−460 (−97,3%)** |

### 3.2 Proporção teste / produção

| Branch | LOC produção | LOC teste | Razão teste:produção |
|---|---|---|---|
| `with-tdd` | 300 | 473 | **1,58 : 1** |
| `without-tdd` | 330 | 13 | **0,04 : 1** |

A abordagem TDD produziu código de produção 10% mais enxuto e, ao mesmo tempo, gerou 36× mais linhas de especificação executável (testes) do que a abordagem sem TDD.

---

## 4. Métricas de Commits

| Categoria | `with-tdd` | `without-tdd` |
|---|---|---|
| Commits totais na branch | 15 | 8 |
| `feat` | 9 | 4 |
| `docs` | 4 | 3 |
| `fix` | 1 | 0 |
| `chore` | 1 | 0 |
| Commits por ciclo implementado | ~1,1 | ~0,5 |

A branch `without-tdd` tem menos commits de implementação por ciclo. Isso não reflete maior eficiência — reflete ausência de iteração RED → GREEN → REFACTOR. Em TDD cada ciclo naturalmente gera ao menos dois commits (teste falhando + implementação passando); sem TDD a implementação vai direto ao código final.

---

## 5. Documentação Compensatória

A ausência de testes como especificação executável criou a necessidade de documentação adicional para substituí-los:

| Artefato | Propósito | Existe em `with-tdd` | Existe em `without-tdd` |
|---|---|---|---|
| `docs/specs/*.yaml` (8 arquivos) | Contratos com casos de teste declarativos | Sim (pré-TDD) | Sim (como substituto de testes) |
| `docs/specs/*.md` (8 arquivos) | Especificações narrativas com checklist | Sim | Sim |
| `IMPLEMENTATION_PLAN_*.md` | Plano de ordem de implementação e riscos | Sim (144 linhas) | **Sim (133 linhas) — novo** |
| Skill `feature-implementation` | Protocolo de tradução do contrato YAML em código | Não usado | **Usado em cada ciclo** |

O documento `IMPLEMENTATION_PLAN_CALCULADORA_SALARIO_SEM_TDD_20260513.md` não existia na branch TDD porque o ciclo RED → GREEN já exercia essa função de planejamento explícito. Sem TDD, foi necessário formalizar em texto o que viria a ser a sequência de implementação e os riscos por classe — esforço que consome tempo e não é verificável automaticamente.

---

## 6. Pontos de Atrito Específicos do Desenvolvimento sem TDD

### 6.1 Ausência de feedback imediato sobre contratos de API

Em TDD, o teste é escrito antes do método existir. Se a assinatura do método estiver errada, o teste não compila — o erro é imediato e localizado. Sem TDD, a assinatura só é confrontada com o uso real na integração.

**Exemplo concreto desta branch:** `TuiAnimator` precisava que `stop()` interrompesse o thread criado em `start()`. A spec dizia "Thread como variável local em `start()`", o que é incompatível com `stop()` acessando o mesmo thread. Em TDD, o teste de `stop()` teria revelado a contradição antes da implementação. Aqui, a inconsistência foi identificada na análise textual do contrato — tardia e sem garantia de que foi a única.

### 6.2 Verificação manual substitui execução de testes

Cada ciclo sem TDD termina com `mvn compile` como único gate automatizado. A compilação verifica sintaxe, não comportamento. Casos de borda — vírgula como separador decimal, IRRF negativo, salário zero — precisaram ser verificados por inspeção do código contra os casos de teste do YAML, sem execução.

O plano de implementação registra explicitamente esse risco:

> *"Sem testes guiando o design, desvios de contrato (arredondamento, mensagens, layout) só aparecem em execução manual."*

### 6.3 Verbosidade maior em classes de validação

`InputParser` tem 61% mais linhas na branch sem TDD (29 vs 18). Em TDD, cada teste exercita exatamente um caso — isso pressiona o desenvolvedor a extrair métodos pequenos para que cada caso seja testável isoladamente. Sem esse pressão, a implementação tende a resolver todos os casos em um bloco único mais longo.

### 6.4 Ausência de rede de segurança para refatoração

A etapa de **REFACTOR** do ciclo TDD é possível com segurança porque os testes detectam regressões. Sem testes, qualquer simplificação ou renomeação carrega risco de quebra silenciosa. O resultado prático é que a etapa de refatoração é omitida ou feita com cautela excessiva — código que poderia ser simplificado permanece como está para não introduzir riscos.

### 6.5 Especificações YAML não são executáveis

Os arquivos `docs/specs/*.yaml` definem casos de teste como:

```yaml
test_cases:
  - input: "3000,50"
    expected: GrossSalary(3000.50)
    note: vírgula como separador decimal
```

Isso é documentação, não código. A garantia de que a implementação satisfaz esse caso depende inteiramente de disciplina humana no momento da revisão. Em TDD, esse mesmo caso seria `@Test void shouldAcceptCommaAsDecimalSeparator()` — verificável, rastreável e reexecutável a qualquer momento.

---

## 7. Comparativo de Processo por Ciclo

| Etapa | `with-tdd` | `without-tdd` |
|---|---|---|
| 1. Especificação | Escrever spec `.md` + `.yaml` | Escrever spec `.md` + `.yaml` |
| 2. Definição de comportamento | Escrever teste (RED) | Ler contrato YAML manualmente |
| 3. Feedback de API | Compilação do teste falha → API errada | Compilação do código de produção |
| 4. Implementação | Fazer teste passar (GREEN) | Implementar conforme contrato |
| 5. Verificação de correção | Teste passa | Inspeção do código vs YAML |
| 6. Refatoração segura | Sim — testes detectam regressões | Limitada — sem rede de segurança |
| 7. Documentação gerada | Teste = especificação viva | Checklist `.md` marcado manualmente |

---

## 8. Indicadores de Qualidade Comparados

| Indicador | `with-tdd` | `without-tdd` |
|---|---|---|
| Comportamento verificado automaticamente | Sim (38 cenários) | Não (0 cenários) |
| Cobertura de edge cases | Explícita em testes | Implícita em YAML, não executada |
| Detecção de regressão | Automática | Manual / inexistente |
| API design guiada pelo uso | Sim (teste-first) | Não (implementation-first) |
| Refatoração segura | Sim | Não |
| Documentação viva do comportamento | Testes + specs | Specs apenas |
| Planejamento adicional necessário | Não | Sim (`IMPLEMENTATION_PLAN`) |

---

## 9. Análise das Diferenças de Implementação

Três classes apresentaram diferença significativa de tamanho entre as branches:

**`IrrfTable` (+45% mais linhas sem TDD)**  
A tabela IRRF tem 5 faixas com parcelas dedutíveis — o maior risco de erro lógico do projeto. Em TDD, 4 testes com valores reais de cada faixa forçaram a implementação a ser exatamente correta. Sem TDD, a implementação foi escrita de uma vez e verificada visualmente, resultando em código mais defensivo e, consequentemente, mais longo.

**`InputParser` (+61% mais linhas sem TDD)**  
`InputParser` é a classe com maior variedade de casos de erro — nulo, vazio, não-numérico, zero, negativo. Em TDD, cada caso de erro tem um teste próprio, o que incentiva guard clauses curtas. Sem TDD, a implementação acumulou constantes de mensagem, blocos try/catch e comentários implícitos no próprio código, aumentando o volume.

**`TuiAnimator` (+19% mais linhas sem TDD)**  
A contradição de design na spec (thread local vs campo para `stop()`) foi absorvida na implementação com um campo adicional `thread` não previsto formalmente. Em TDD, o teste de `stop()` teria exposto a necessidade de um campo antes da implementação começar.

---

## 10. Conclusões

1. **A ausência de testes não elimina a necessidade de especificação** — ela apenas a desloca para documentos YAML e planos de implementação que não podem ser executados automaticamente. O esforço de especificação foi equivalente; o retorno em garantia de correção foi drasticamente menor.

2. **O código de produção sem TDD é 10% mais longo** apesar de ter menos cobertura. Isso se deve à maior verbosidade nas classes de validação (`InputParser`, `IrrfTable`), onde a ausência de testes como pressão de design resulta em implementações menos enxutas.

3. **Contradições na especificação emergem mais tarde sem TDD.** O caso `TuiAnimator` (thread local vs campo acessível) foi detectado em análise textual — em TDD seria detectado em compilação do teste, antes de qualquer código de produção existir.

4. **Refatoração se torna um risco em vez de uma etapa natural.** Sem testes detectando regressões, a etapa REFACTOR é suprimida ou executada com cautela excessiva, preservando code smells que o ciclo TDD eliminaria sistematicamente.

5. **O gate de qualidade se reduz a `mvn compile`.** Compilação verifica sintaxe; não verifica arredondamento financeiro, mensagens de erro em português, formatação do painel, comportamento do spinner ou fluxo de retry de entrada inválida. Todos esses comportamentos ficaram sem verificação automatizada.
