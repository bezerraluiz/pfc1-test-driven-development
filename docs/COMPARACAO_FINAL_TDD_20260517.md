# TDD vs. Desenvolvimento Tradicional: Análise Comparativa de um Projeto Java Real

**Projeto:** Calculadora de Salário Líquido  
**Branches analisadas:** `with-tdd` · `without-tdd`  
**Stack:** Java 21 · Spring Boot 4.0.6 · JUnit 5 · Maven  
**Data da análise:** 17/05/2026  
**Referências:** `METRICAS_TDD_20260513.md` · `METRICAS_SEM_TDD_20260514.md`

---

## 1. Introdução

A discussão sobre o valor prático do Test-Driven Development (TDD) frequentemente oscila entre dois extremos: defensores que o tratam como condição necessária para qualidade de software, e céticos que o enxergam como sobrecarga desnecessária em projetos com prazos reais. Avaliações puramente teóricas tendem a não resolver essa tensão porque ignoram o atrito concreto de cada abordagem no dia a dia do desenvolvimento.

Este documento apresenta uma comparação direta entre duas implementações do mesmo sistema — uma Calculadora de Salário Líquido em Java — desenvolvidas em paralelo, em branches separadas, com o único diferencial metodológico sendo a presença ou ausência de TDD. O objetivo não é repetir a literatura sobre o tema, mas documentar o que de fato divergiu entre as duas abordagens ao longo do ciclo de desenvolvimento: cobertura, tamanho de código, qualidade de design, pontos de atrito e garantias entregues ao final.

---

## 2. Contexto e Metodologia

O projeto implementa os 8 componentes de uma calculadora de salário líquido, do núcleo de cálculo fiscal à interface de terminal (TUI): tabelas INSS e IRRF, modelo de domínio, parser de entrada, formatador, animador de terminal e orquestrador de fluxo.

Ambas as branches partiram das mesmas especificações — documentos `.md` e contratos `.yaml` em `docs/specs/` — eliminando viés de escopo. A diferença estrutural está no momento e na função dos testes:

- **`with-tdd`:** testes escritos *antes* da implementação, guiando o design via ciclo RED → GREEN → REFACTOR, ao longo de ~7 dias de desenvolvimento ativo (07–13/05/2026).
- **`without-tdd`:** sem testes escritos durante o desenvolvimento; os contratos YAML serviram de especificação declarativa traduzida manualmente em código, concluída em ~2 dias (13–14/05/2026).

---

## 3. Cobertura de Testes: O Contraste Central

A diferença mais objetiva entre as duas abordagens é quantitativa e categórica.

| Métrica | `with-tdd` | `without-tdd` | Variação |
|---|---|---|---|
| Classes de teste | 10 | 1 | −90% |
| Métodos de teste | 38 | 1 | −97,4% |
| Testes unitários | 33 | 0 | −100% |
| Testes de integração | 5 | 0 | −100% |
| Smoke test de contexto | 1 | 1 | 0 |

O único teste na branch `without-tdd` é o `contextLoads()` gerado automaticamente pelo Spring Boot — ele verifica que a aplicação sobe, não que ela calcula corretamente. Na prática, ao final do desenvolvimento sem TDD, **zero comportamentos de negócio estão verificados automaticamente**: nem arredondamento financeiro, nem fluxo de retry de entrada inválida, nem formatação do painel em português, nem o comportamento do spinner de terminal.

Os 38 testes da branch `with-tdd` cobrem 21 cenários de fluxo normal, 7 casos de borda e 10 cenários de erro — um mapeamento executável de todo o espaço de comportamento esperado do sistema.

---

## 4. Tamanho e Qualidade do Código de Produção

Contra a intuição de que escrever testes primeiro consome tempo e resulta em mais código, os dados apontam na direção oposta.

| Branch | LOC produção | LOC testes | Razão teste:produção |
|---|---|---|---|
| `with-tdd` | 300 | 473 | 1,58 : 1 |
| `without-tdd` | 330 | 13 | 0,04 : 1 |

O código de produção sem TDD é **10% mais longo** apesar de entregar menos garantias. Três classes explicam a divergência:

**`InputParser` (+61% de linhas sem TDD).** É a classe com maior variedade de casos de erro — entrada nula, vazia, não-numérica, zero, negativa. Em TDD, cada caso tem um teste próprio que pressiona o desenvolvedor a escrever guard clauses curtas e isoladas. Sem esse pressão, a implementação acumulou blocos try/catch mais longos, constantes de mensagem e verificações combinadas, aumentando o volume sem ampliar o comportamento.

**`IrrfTable` (+45% de linhas sem TDD).** A tabela IRRF é o componente de maior risco lógico do projeto — cinco faixas com parcelas dedutíveis acumuladas. Os 4 testes da branch TDD forçaram cada faixa a ser verificada com valores reais. Sem essa pressão, a implementação foi escrita defensivamente de uma vez, resultando em código mais longo e verificado apenas por inspeção visual.

**`TuiAnimator` (+19% de linhas sem TDD).** A especificação definia `Thread` como variável local em `start()`, incompatível com o acesso necessário em `stop()`. Em TDD, o teste de `stop()` teria revelado a contradição antes de qualquer código existir. Sem TDD, a inconsistência foi absorvida com um campo adicional não previsto formalmente — detectada em análise textual, não em compilação.

---

## 5. Processo por Ciclo: Onde os Caminhos Divergem

A comparação ciclo a ciclo revela que o esforço de especificação foi *equivalente* nas duas abordagens — ambas produziram 8 documentos `.md` e 8 contratos `.yaml`. A diferença está no retorno desse esforço.

| Etapa do ciclo | `with-tdd` | `without-tdd` |
|---|---|---|
| Definição de comportamento | Escrever teste (RED) | Ler contrato YAML manualmente |
| Feedback de API | Compilação do teste falha — API errada | Compilação do código de produção |
| Verificação de correção | Teste passa (GREEN) | Inspeção do código vs. YAML |
| Refatoração segura | Sim — testes detectam regressões | Limitada — sem rede de segurança |
| Documentação gerada | Teste = especificação viva | Checklist `.md` marcado manualmente |

O ponto crítico é a **verificação de correção**. No ciclo TDD, a pergunta "este código faz o que foi especificado?" tem resposta binária e automática: o teste passa ou não. Sem TDD, a mesma pergunta é respondida por um humano comparando o código contra um YAML — processo que não escala, não é reexecutável e não detecta regressões futuras.

A branch sem TDD também exigiu a criação de um `IMPLEMENTATION_PLAN` de 133 linhas ausente na branch TDD. O plano substitui em texto o que o ciclo RED → GREEN já resolve por construção: a ordem de implementação e os riscos por classe. É documentação adicional que consome tempo sem ser verificável automaticamente.

---

## 6. Commits e Rastreabilidade

| Categoria | `with-tdd` | `without-tdd` |
|---|---|---|
| Commits totais | 15 | 8 |
| `feat` | 9 | 4 |
| Commits por ciclo | ~1,1 | ~0,5 |

A branch sem TDD tem menos commits de implementação por ciclo. Não por eficiência maior, mas por ausência de iteração — commits que na abordagem TDD separam claramente o teste falhando da implementação passando, aqui são agrupados. O commit `feat: implementar domínio de calculadora de salário bruto e líquido`, por exemplo, cobre os ciclos 3 e 4 juntos, reduzindo a granularidade e a rastreabilidade do histórico.

---

## 7. Discussão: O que os Dados Revelam

### 7.1 Especificação não é opcional — ela muda de forma

Talvez o achado mais relevante desta comparação seja que **a ausência de TDD não elimina o esforço de especificação**. Ambas as branches produziram volume equivalente de documentação prévia. O que muda é o destino desse esforço: na branch TDD, a especificação se torna código executável (testes); na branch sem TDD, permanece como texto — útil para o desenvolvedor original, opaco para o sistema de CI e perecível ao longo do tempo.

### 7.2 O custo do TDD está no início; o custo da ausência, no futuro

A branch `without-tdd` foi concluída em ~2 dias versus ~7 dias na branch TDD. Se o horizonte de análise for o desenvolvimento inicial, o desenvolvimento sem TDD parece mais eficiente. O problema é que esse cálculo ignora o custo de manutenção: qualquer alteração futura em `IrrfTable`, `InputParser` ou `TuiAnimator` na branch sem TDD será feita sem rede de segurança. Regressões introduzidas por refatoração ou nova feature só serão detectadas em execução manual — se forem detectadas.

### 7.3 Design guiado pelo uso vs. design guiado pela implementação

Os dados de tamanho de código apontam para um efeito de design do TDD frequentemente citado na literatura, mas raramente documentado com números concretos: classes desenvolvidas test-first tendem a ser menores porque os testes pressionam por interfaces simples e responsabilidades isoladas. `InputParser` com 18 linhas versus 29 linhas não é coincidência — é o resultado de 6 testes que exercitam cada caso de erro separadamente, tornando guard clauses longas explicitamente difíceis de testar.

---

## 8. Conclusões

**1. Cobertura não é overhead — é o produto principal do TDD.**  
Os 38 testes da branch `with-tdd` não são subproduto do desenvolvimento; eles *são* a especificação executável do sistema. A branch `without-tdd` entregou o mesmo software funcionando, porém sem evidência verificável de que ele funciona corretamente. Para sistemas financeiros — onde arredondamento e faixas de tributação devem ser precisos — essa distinção é material.

**2. Código sem testes não é mais enxuto.**  
O argumento de que "escrever testes leva mais tempo e gera mais código" não se sustenta nos dados: a branch TDD produziu 10% menos código de produção com 36× mais especificação executável. O código extra na branch sem TDD é atrito de validação acumulado em implementações sem pressão de testabilidade.

**3. TDD antecipa erros de design, não apenas erros de lógica.**  
O caso `TuiAnimator` demonstra que testes escritos antes da implementação revelam contradições na especificação em tempo de compilação. Sem TDD, a mesma contradição foi detectada em análise textual — mais tarde, sem garantia de que foi a única, e sem evidência rastreável no histórico.

**4. Refatoração segura é uma propriedade emergente do TDD.**  
A etapa REFACTOR do ciclo TDD só é segura porque os testes detectam regressões. Sem essa rede, refatoração se torna risco gerenciado por cautela humana — o que na prática significa que ela não acontece, e code smells se acumulam.

**5. A duração menor do desenvolvimento sem TDD é um artefato, não um ganho.**  
Os ~2 dias da branch `without-tdd` versus ~7 dias da `with-tdd` refletem a ausência das etapas RED e REFACTOR, não maior produtividade. O que foi economizado em tempo de desenvolvimento foi transferido para risco de manutenção futuro — um trade-off implícito que raramente aparece na contabilidade de projetos.

---

## Apêndice: Tabela-Resumo dos Indicadores

| Indicador | `with-tdd` | `without-tdd` |
|---|---|---|
| Comportamentos verificados automaticamente | 38 cenários | 0 cenários |
| LOC produção | 300 | 330 (+10%) |
| LOC testes | 473 | 13 (−97,3%) |
| Razão teste:produção | 1,58 : 1 | 0,04 : 1 |
| Cobertura de edge cases | Explícita em testes | Implícita em YAML, não executada |
| Detecção de regressão | Automática | Manual / inexistente |
| Design guiado pelo uso | Sim | Não |
| Refatoração segura | Sim | Não |
| Planejamento adicional necessário | Não | Sim (+133 linhas) |
| Duração dos ciclos ativos | ~7 dias | ~2 dias |
| Commits de feature | 9 | 4 |
| Gate de qualidade final | 38 testes + compile | `mvn compile` |
