---
date: 16-04-2026
name: feature-implementation
description: Implementa uma feature a partir de um `contrato.yaml`, tratando o contrato como fonte de verdade para testes, código, integrações e validação final. Use quando a SPEC já estiver pronta e for necessário desenvolver a feature na codebase atual com TDD, sem reinventar escopo, regras ou fluxos fora do contrato.
tags:
  - skill/feature-implementation
  - implementation
  - tdd
  - yaml
disable-model-invocation: true
---

# Feature Implementation

Executa o desenvolvimento de uma feature já especificada em `contrato.yaml`.

## Entradas

| Entrada | Obrigatório | Fonte | Descrição |
| --- | --- | --- | --- |
| `contrato` | Sim | arquivo informado pelo usuário | Caminho do `contrato.yaml` da feature |

## Objetivo

Entregar a feature funcionando com base no `contrato.yaml`, seguindo esta ordem obrigatória:

1. ler e validar o contrato;
2. escrever ou ajustar testes para representar o contrato;
3. coverage de testes ~95%;
4. refatorar sem alterar o comportamento contratado;
5. validar coerência final entre contrato, testes e código.

O `contrato.yaml` é a fonte da verdade. A implementação não deve inventar comportamento fora dele. **A SPEC (contrato) é a autoridade máxima** - qualquer decisão fora do escopo definido deve ser registrada como lacuna ou questionada antes de ser implementada.

## Princípios

- tratar o contrato como escopo fechado da implementação;
- Sempre usar TDD: `red`, `green`, `refactor`;
- preservar padrões já existentes da codebase;
- registrar suposições ou lacunas explicitamente;

### Princípios de Desenvolvimento (OBRIGATÓRIOS)

Estes princípios devem ser seguidos em toda implementação:

- **KISS** - Manter simplicidade operacional: resolver o problema atual com o menor número de partes possível, preferir clareza à esperteza, reduzir abstrações desnecessárias
- **YAGNI** - Não implementar antecipadamente: construir apenas o que o problema atual exige, adiar abstrações até necessidade comprovada
- **DRY** - Evitar duplicação de conhecimento: cada regra importante deve ter uma fonte principal de verdade, consolidar repetição real, não criar abstrações prematuras
- **Less Code, Best Code** - Menos código é melhor código: cada linha adicionada é uma linha que precisa ser lida, mantida e testada. Preferir deletar código a adicionar. A melhor solução geralmente é a que resolve o problema com menos código.

### Object Calisthenics (OBRIGATÓRIOS)

Regras de design que forçam código orientado a objetos com alta coesão e baixo acoplamento:

1. **Um nível de indentação por método** — se há mais de um nível, extrair método
2. **Não usar `else`** — usar early return, guard clauses ou polimorfismo
3. **Encapsular primitivos e strings** — tipos com regras de negócio devem ser Value Objects
4. **Coleções de primeira classe** — uma classe que contém uma coleção não deve ter outros atributos
5. **Um ponto por linha** — `a.b.c()` viola a Lei de Demeter; encapsular a navegação
6. **Não abreviar** — nomes devem ser descritivos; abreviação = falta de clareza
7. **Manter entidades pequenas** — máximo ~50 linhas por classe, ~10 linhas por método
8. **No máximo duas variáveis de instância por classe** — forçar decomposição e coesão
9. **Sem getters/setters** — comportamento deve estar no objeto que possui os dados; não expor estado raw

## Fluxo De Trabalho

### 1. Ler o `contrato.yaml` e classificar risco

Extrair do contrato, no mínimo:

- `feature`;
- `description`;
- `rules`;
- `input`;
- `output`;
- `errors`.

Também identificar, se existirem:

- eventos;
- integrações externas;
- restrições de compatibilidade;
- observações operacionais.

Se faltar informação essencial para implementar com segurança, interromper a execução e apontar exatamente o campo ausente ou ambíguo.

#### Classificação de Risco de Operações Destrutivas

**Keywords de detecção:** `DROP`, `TRUNCATE`, `DELETE` (sem WHERE), `ALTER...DROP COLUMN`, `rm -rf`, `rmdir`, `unlink` (path dinâmico), `terraform destroy`, `kubectl delete`, `docker volume rm`, `migrate` (schema), `purge`, `wipe`, `--force` em comando destrutivo, `git push --force`, `git reset --hard`

**Classificação de severidade:**

| Severidade | Exemplos |
|------------|----------|
| **CRITICAL** | DELETE-all / DROP / TRUNCATE sem guarda em tabelas de dados de usuário |
| **HIGH** | Migration sem DOWN, `rm -rf` com path variável, force flags em comandos destrutivos |
| **MEDIUM** | Migration sem rollback testado, cascade delete sem documentação de escopo |

**Quando detectada operação destrutiva, documentar obrigatoriamente as 5 medidas:**

| # | Medida | O que documentar |
|---|--------|-----------------|
| 1 | **Backup plan** | O que fazer backup e como verificar completude |
| 2 | **Rollback plan** | Procedimento de desfazer, testado em não-produção |
| 3 | **Blast radius** | Recursos afetados + escopo estimado + downtime |
| 4 | **Environment guard** | Bloqueado por env check ou confirmação de admin |
| 5 | **Preview / dry-run** | Output what-if, SQL diff, `terraform plan`, `SELECT COUNT(*)` antes do DELETE |

Severidade **CRITICAL** exige parar e confirmar com o usuário antes de prosseguir (HITL Gate).

### 2. Transformar contrato em checklist executável

Antes de codar, converter o contrato em itens verificáveis:

- regras de negócio que precisam existir no código;
- entradas que precisam ser validadas;
- saídas que precisam ser produzidas;
- erros que precisam ser retornados ou propagados;
- fluxos e efeitos colaterais esperados.

Nada deve ser implementado sem estar mapeado para um item concreto do contrato.

### 3. Planejar os testes a partir do contrato

Derivar os testes diretamente do `contrato.yaml`.

Cobrir no mínimo:

- cenário feliz;
- validações de input;
- regras de negócio;
- erros esperados;
- regressões prováveis;
- integrações afetadas, se houver.

Escolher o nível mais adequado para cada validação:

- unitário para regra isolada;
- integração para fluxo entre camadas;
- contrato para entradas e saídas estruturadas;
- E2E ou fluxo para comportamento ponta a ponta quando necessário.

### 4. Executar `red -> green -> refactor`

#### Red

- escrever primeiro os testes que representam o contrato;
- confirmar que falham pelo motivo correto;
- evitar testes genéricos que não provam o comportamento contratado.

#### Green

- implementar apenas o necessário para fazer os testes passarem;
- alterar o menor número possível de arquivos;
- manter nomes, fluxos e erros alinhados ao contrato.

#### Refactor

- simplificar o código depois dos testes verdes;
- remover duplicação e código provisório;
- preservar integralmente o comportamento validado.

### 5. Validar coerência final

Antes de concluir, conferir:

- toda regra do contrato existe em testes e código;
- todos os inputs esperados estão validados;
- todos os outputs esperados estão produzidos;
- todos os erros definidos estão tratados;
- a implementação não adicionou comportamento fora do contrato;
- os testes executados cobrem o que foi implementado.

## Formato Da Saída

Entregar a resposta em Markdown com esta estrutura mínima:

```markdown
## Contrato executado

- Feature: `create user`
- Objetivo: implementar o cadastro de usuário com validação mínima e unicidade de e-mail
- Caminho do contrato: `/specs/users/create-user.yaml`
- Suposições: o repositório já possui infraestrutura de persistência e handler de erro padrão

## Contexto encontrado na codebase

- `src/modules/users/controller.ts` - endpoint de usuários
- `src/modules/users/service.ts` - regra de negócio do domínio
- `src/modules/users/repository.ts` - busca por e-mail existente
- `tests/users/create-user.test.ts` - padrão de testes do módulo
- `src/shared/errors/AppError.ts` - classe base para erros
- `src/shared/container/index.ts` - injeção de dependência
- `src/database/typeorm/migrations/` - migrations para users
- `tests/utils/test-utils.ts` - utilitários para mock
- `tests/factories/users.factory.ts` - factory para usuários
- `jest.config.js` - configuração de testes com coverage

## Implementação realizada

- **Testes**: sucesso, erro EMAIL_ALREADY_EXISTS, input inválido
- **Código**: validação de payload, verificação de unicidade, retorno conforme output do contrato
- **Alinhamento**: rules → validação/serviço, input → boundary, output → retorno, errors → tratamento

## Resultados dos testes

| Métrica | Valor |
|---------|-------|
| Total | 12 |
| Passed | 10 |
| Failed | 1 |
| Skipped | 1 |
| Coverage | statements 85%, branches 78%, functions 80%, lines 82% |

## Validação final

- Testes executados: `npm test -- users/create-user`
- Checks adicionais: lint do módulo
- Riscos residuais: cobertura E2E não adicionada
- Itens não realizados: nenhum
```

## Regras Críticas

- Não implementar nada que não esteja sustentado pelo `contrato.yaml`.
- Não ignorar conflito entre contrato e comportamento atual; registrar e resolver de forma explícita.
- Não pular testes quando a feature altera comportamento verificável.
- Não reescrever arquitetura do projeto sem necessidade clara do contrato.
- Não commitar as alterações.
- Proibido usar `any` em qualquer ponto do código. Toda tipagem deve ser explícita e correta. 
- Não usar workarounds ou gambiarras para contornar erros — resolver sempre a causa raiz.
- Não deixar código legado.

## Definição De Pronto

O trabalho está pronto quando:

1. o `contrato.yaml` tiver sido lido e convertido em checklist executável;
2. os testes representarem o comportamento contratado;
3. a implementação fizer os testes passarem;
4. o código final permanecer coerente com o contrato;
5. os riscos, suposições e limitações estiverem explícitos;
6. a documentação markdown da feature tiver seu checklist marcado como concluído.

### Marcação do Checklist da Documentação

Ao final da implementação, antes de finalizar o trabalho:

1. Localizar o arquivo de documentação markdown da feature (geralmente em `docs/features/` ou similar);
2. Encontrar a seção de checklist (itens com `- [ ]`);
3. Marcar todos os itens como concluídos alterando `- [ ]` para `- [x]`;
4. Se a documentação não existir, criar a estrutura básica de checklist baseada no contrato.
