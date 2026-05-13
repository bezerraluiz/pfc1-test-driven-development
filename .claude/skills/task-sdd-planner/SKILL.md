---
date: 16-04-2026
name: task-sdd-planner
description: Monta um planejamento de implementação orientado por SDD a partir de uma task (feature, bug, fix, melhoria, implementação do zero) e do desenvolvedor responsável informados pelo usuário, lendo seletivamente a codebase relacionada. Use quando for preciso transformar uma task em um plano executável com ESPECIFICAÇÕES como fonte da verdade.
tags:
  - skill/task-sdd-planner
  - planning
  - sdd
disable-model-invocation: true
---

# Task SDD Planner

Gera um plano de implementação antes do código. A task e o desenvolvedor responsável são informados como parâmetros. A codebase atual é a fonte de contexto. As **ESPECIFICAÇÕES** producedas por este plano são a **FONTE DA VERDADE** — tudo que vier depois deve estar 100% fundamentado nelas.

## Entradas

| Entrada | Obrigatório | Fonte | Descrição |
| --- | --- | --- | --- |
| `task` | Sim | pedido do usuário | Task (feature, bug, fix, melhoria ou implementação do zero) a ser planejada, pode ser em formato de texto ou arquivo mencionado |
| `desenvolvedor` | Sim | pedido do usuário | Nome do desenvolvedor responsável pela implementação |
| `branch` | Sim | pedido do usuário | Branch onde a task será implementada |
| `codebase` | Sim | workspace atual | Projeto onde a task será implementada |
| `docs_path` | Não | workspace atual (padrão: `docs/`) | Pasta onde será salvo o documento de implementação |

*Caso falte algum parâmetro de entrada, deve parar a execução e solicitar ao usuário o preenchimento de todos os parâmetros.*

## Objetivo

Produzir um plano de implementação seguindo este fluxo obrigatório:

1. **Ler a task** — entender o que foi pedido, delimitar escopo, registrar suposições.
2. **Ler a codebase** — identificar arquivos, padrões, contratos e pontos de impacto relacionados à task.
3. **Montar as ESPECIFICAÇÕES** — consolidar tudo que foi lido em um plano executável. As SPECS são a fonte da verdade; nenhuma decisão de implementação deve existir fora delas.
4. **Criar documento de implementação** — salvar o plano em um arquivo markdown na pasta `docs/` do projeto.

Não inverter a ordem. Não propor implementação antes de explicitar as especificações.

## Fluxo de Trabalho

### 1. Ler a task

- Resumir a task em uma frase objetiva.
- Identificar o resultado esperado e o critério de aceite.
- Delimitar escopo e fora de escopo.
- Registrar suposições quando houver ambiguidade — adotar a interpretação mais conservadora.

### 2. Ler a codebase relacionada

Investigar o mínimo necessário para entender onde a task toca.

Começar por:

- busca textual por termos da feature, nomes de domínio, endpoints, eventos, labels de UI e entidades;
- arquivos de contrato, documentação, testes e implementações próximas;
- pontos de entrada prováveis: rotas, controllers, handlers, services, casos de uso, componentes, repositórios, schemas.

Durante a leitura:

- usar `rg` para localizar arquivos, símbolos e termos;
- abrir somente os arquivos que confirmam comportamento, padrões, contratos e impacto;
- anotar caminhos concretos que justificam cada parte do plano;
- distinguir claramente o que é fato observado e o que é inferência.

Se não houver relação clara com a task, registrar a lacuna em vez de inventar contexto.

### 3. Montar as Especificações

Com base exclusivamente no que foi lido, produzir o plano no formato abaixo. As especificações são a **FONTE DA VERDADE**: qualquer implementação, teste ou decisão posterior deve referenciar e respeitar o que está aqui. Se algo não estiver especificado, não deve ser implementado.

### 4. Criar documento de implementação

Ao final do planejamento, criar um arquivo markdown na pasta `docs/` do projeto com o nome:

```
IMPLEMENTATION_PLAN_{NOME_DA_TASK}.md
```

Exemplo: `docs/IMPLEMENTATION_PLAN_LOGIN_GOOGLE_20260416_143022.md`

O documento deve conter o plano completo gerado na etapa anterior.

## Formato da Saída

Entregar a resposta em Markdown com esta estrutura:

```markdown
# Plano de implementação - {NOME DA TASK}

> {data de criação}
> Branch: {branch informado}
> Desenvolvedor responsável: {nome do desenvolvedor}

## Resumo sobre a task

- objetivo da task em uma frase;
- resultado esperado e critério de aceite;
- escopo (o que está dentro);
- fora de escopo (o que não será feito).

## Padrões a preservar

- convenções de código, arquitetura e nomenclatura observadas na codebase;
- contratos de API, schemas ou estruturas de dados que devem ser mantidos;
- padrões de teste existentes.

## Arquivos existentes que se relacionam com a task

- lista de arquivos com caminho real e breve descrição da relação com a task;
- distinguir fatos observados de inferências.

## Dependências externas

- bibliotecas, serviços, APIs ou integrações necessárias;
- pré-condições que precisam existir antes da implementação.

## Ordem recomendada de implementação

- sequência sugerida de passos, do menor risco ao maior;
- indicar onde escrever testes antes do código (TDD) quando aplicável;
- registrar riscos técnicos e pontos de atenção por etapa.
```

## Regras de Qualidade

- Referenciar caminhos reais do projeto sempre que possível.
- Sinalizar claramente fatos observados versus inferências.
- Evitar plano genérico; conectar cada seção com o que foi lido na codebase.
- Evitar overengineering; preferir o menor desenho que entregue a task.
- Registrar lacunas sem bloquear desnecessariamente o planejamento.
- Nunca propor implementação fora do escopo especificado.

## Definição de Pronto

O plano está pronto quando outro engenheiro consegue, sem reinventar o escopo:

1. escrever ou revisar documentação e contrato a partir do **Resumo da task**;
2. criar testes que falham com base nas especificações;
3. implementar somente o que está na **Ordem recomendada**;
4. revisar coerência, regressão e qualidade antes do merge;
5. se a review reprovar algo, voltar às SPECS e repetir o fluxo.
