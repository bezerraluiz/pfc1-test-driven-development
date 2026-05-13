---
date: 16-04-2026
name: spec-document-writer
description: Cria os dois artefatos de SPEC de uma feature, `documentacao.md` para desenvolvedores e `contrato.yaml` para IA, a partir de uma descrição funcional, regra de negócio ou backlog. Use quando for preciso transformar uma feature em documentação humana e contrato operacional estruturado, mantendo ambos sincronizados e coerentes.
tags:
  - skill/spec-document-writer
  - spec
  - markdown
  - yaml
disable-model-invocation: true
---

# Spec Document Writer

Gera os dois documentos-base de uma feature:

- `documentacao.md`: spec voltada para humanos, usada pelos devs como guia de entendimento e implementação;
- `contrato.yaml`: spec operacional voltada para IA, usada como contrato estruturado para execução da task com o mínimo de ambiguidade.

## Entradas

| Entrada | Obrigatório | Fonte | Descrição |
| --- | --- | --- | --- |
| `plano` | Sim | pedido do usuário | Planejamento das features |

## Objetivo

Produzir uma SPEC dupla e coerente:

1. `documentacao.md` para devs
2. `contrato.yaml` para IA

Os dois documentos devem descrever a mesma feature, sem divergência de regra, input, output, erro ou fluxo.

## Princípios

- Tratar os dois artefatos como duas visões da mesma fonte de verdade.
- Especificar tudo de forma explícita; não deixar comportamento implícito.
- Manter nomes, regras e semântica consistentes entre Markdown e YAML.
- Preferir clareza e objetividade; evitar texto decorativo.
- Se faltar contexto, registrar a suposição em vez de inventar detalhes silenciosamente.

## Fluxo De Trabalho

### 1. Entender a feature

- Resumir a feature em uma frase objetiva.
- Identificar o propósito principal.
- Levantar regras de negócio.
- Identificar input, output, erros e fluxo.

Se alguma dessas partes não vier explícita, inferir com cautela e marcar como suposição.

### 2. Escrever primeiro o `contrato.yaml`

Começar pelo contrato estruturado, porque ele força precisão.

Regras para o YAML:

- usar nomes curtos e semânticos;
- manter `feature` e `description` claros e completos;
- incluir `endpoint` sempre que for uma operação de API REST;
- listar todas as regras de negócio em `rules` de forma precisa, sem ambiguidade e sem duplicar o que já está nos campos;
- mapear os campos de `input` e `output` com `type`, `nullable`, constraints e `description`;
- marcar campos sensíveis com `sensitive: true` no input;
- usar `persist` para documentar exatamente o que vai ao banco e como — especialmente campos derivados (ex: hash de senha);
- listar erros em `errors` com `code`, `http_status`, `message`, `trigger` e `behavior` quando houver ação pós-erro;
- registrar side effects em `side_effects` com `type`, `timing`, `description` e `on_failure`;
- registrar suposições em `assumptions` em vez de inventar silenciosamente;
- não misturar narrativa longa com o contrato.

### 3. Escrever depois a `documentacao.md`

Converter o mesmo conteúdo para uma leitura orientada ao dev.

Regras para o Markdown:

- escrever para desenvolvedores de nível estagiário-sênior, com linguagem clara e direta;
- usar o **Checklist de Implementação** como guia passo a passo para o dev;
- usar tabelas para input, output e errors — facilita leitura rápida;
- usar os mesmos conceitos, nomes e valores do YAML;
- explicar a intenção da feature na seção `Descrição`;
- nunca omitir um erro ou regra presente no contrato;
- evitar adicionar comportamento que não exista no contrato.

### 4. Validar coerência entre os dois documentos

Antes de concluir, conferir:

- se toda regra do YAML aparece no Markdown;
- se todo campo de `input` e `output` existe nos dois documentos;
- se os erros do YAML têm correspondência compreensível no Markdown;
- se o fluxo descrito não contradiz regras, inputs ou outputs;
- se não existe detalhe presente em um documento e ausente no outro sem justificativa.

## Formato Da Saída

Entregar os dois blocos completos, nesta ordem:

1. `contrato.yaml`
2. `documentacao.md`

Usar fenced code blocks com `yaml` e `md`.

## Organização Das Pastas

Cada documentação deve ser separada por pasta dentro de `/specs`, agrupando os arquivos por contexto de domínio ou módulo.

Estrutura esperada:

```text
/specs
    /1-users
	    /1-create-user
	        create-user.yaml
	        create-user.md
	    /2-update-user
		    update-user.yaml
		    update-user.md
    /2-orders
	    /1-create-order
	        create-order.yaml
	        create-order.md
```

Regras para organização:

- cada feature deve viver dentro de uma pasta específica, como `users`, `orders` ou equivalente ao domínio;
- cada pasta deve ser numerada na sequência da implementação.
- os dois artefatos da mesma feature devem ficar lado a lado na mesma pasta;
- o nome-base dos arquivos deve ser o mesmo nos dois formatos, mudando apenas a extensão;

## Regras De Qualidade

- Não inventar campos ou regras sem sinalizar.
- Não usar códigos de erro vagos quando houver uma regra clara.
- Não deixar o YAML mais rico que o Markdown, nem o contrário.
- Preservar alinhamento semântico entre os dois documentos.
- Se houver termos técnicos ou nomes de domínio, reutilizar exatamente os nomes informados pelo usuário.

## Definição De Pronto

O trabalho está pronto quando:

1. o `contrato.yaml` estiver estruturado e coerente;
2. a `documentacao.md` estiver legível e equivalente ao contrato;
3. os dois documentos representarem a mesma feature sem divergência;
4. as suposições, se houver, estiverem explícitas.

Exemplo de output esperado:

**contrato.yaml**

```yaml
feature: create-user
description: Cria um novo usuário no sistema com autenticação por e-mail e senha. O usuário começa com status "inactive" até confirmar o e-mail via link enviado após o cadastro.

endpoint:
  method: POST
  path: /users
  auth_required: false
  idempotent: false

rules:
  - e-mail deve ser normalizado para lowercase antes de qualquer comparação ou persistência
  - e-mail deve ser único no sistema (comparação feita após normalização)
  - e-mail deve estar em formato válido (RFC 5322)
  - nome deve ter entre 2 e 100 caracteres após trim de espaços
  - senha deve ter no mínimo 8 e no máximo 72 caracteres
  - senha deve conter ao menos 1 letra maiúscula, 1 número e 1 caractere especial
  - senha deve ser armazenada como hash bcrypt com cost mínimo 12; nunca persistir texto puro
  - usuário é criado com status "inactive" por padrão; nunca "active"
  - id deve ser gerado pela aplicação como UUID v4; nunca delegar ao banco
  - created_at deve ser gerado no momento da persistência em UTC
  - password e password_hash nunca devem aparecer em nenhuma resposta da API

input:
  name:
    type: string
    required: true
    nullable: false
    min_length: 2
    max_length: 100
    trim: true
    description: Nome completo ou de exibição do usuário
  email:
    type: string
    required: true
    nullable: false
    format: email
    normalize: lowercase
    description: E-mail de autenticação; usado também para o envio de confirmação
  password:
    type: string
    required: true
    nullable: false
    min_length: 8
    max_length: 72
    pattern: "^(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9]).+$"
    sensitive: true
    description: Senha em texto puro; será hasheada com bcrypt antes de persistir

output:
  id:
    type: string
    format: uuid-v4
    nullable: false
    description: Identificador único do usuário gerado pela aplicação
  name:
    type: string
    nullable: false
    description: Nome do usuário conforme fornecido (após trim)
  email:
    type: string
    nullable: false
    description: E-mail normalizado para lowercase
  status:
    type: enum
    values: [inactive, active]
    nullable: false
    description: Estado do usuário; sempre "inactive" na criação
  created_at:
    type: string
    format: "YYYY-MM-DDTHH:mm:ssZ"
    nullable: false
    description: Data e hora de criação do registro em UTC (ISO 8601)

persist:
  table: users
  fields:
    - name: id
      source: gerado pela aplicação (UUID v4)
    - name: name
      source: input.name após trim
    - name: email
      source: input.email após normalização lowercase
    - name: password_hash
      source: bcrypt(input.password, cost=12)
    - name: status
      source: valor fixo "inactive"
    - name: created_at
      source: timestamp UTC no momento da persistência
  note: password nunca é persistido; somente password_hash

errors:
  - code: INVALID_INPUT
    http_status: 422
    message: "Os dados enviados são inválidos."
    trigger: qualquer campo ausente, nulo ou com valor fora do formato esperado (tamanho, tipo, formato de e-mail)
    fields_affected: [name, email, password]
    note: não cobre força da senha — para isso, usar WEAK_PASSWORD

  - code: WEAK_PASSWORD
    http_status: 422
    message: "A senha não atende aos requisitos mínimos de segurança."
    trigger: password presente e com formato válido, mas sem ao menos 1 maiúscula, 1 número ou 1 caractere especial, ou fora do intervalo 8–72 chars

  - code: EMAIL_ALREADY_EXISTS
    http_status: 409
    message: "O e-mail informado já está em uso."
    trigger: e-mail normalizado já encontrado na base de dados

  - code: EMAIL_DELIVERY_FAILED
    http_status: 500
    message: "Usuário criado, mas falha ao enviar e-mail de confirmação."
    trigger: persistência bem-sucedida seguida de erro no serviço de e-mail
    behavior: não reverter a criação; usuário existe e pode solicitar reenvio posteriormente

side_effects:
  - type: event
    name: user.created
    timing: sync
    description: publicar no message broker após persistência bem-sucedida
    payload: [id, email, created_at]
    on_failure: logar erro; não reverter criação

  - type: email
    name: confirmation-email
    timing: async
    description: disparar e-mail de confirmação após publicação do evento user.created
    on_failure: retornar EMAIL_DELIVERY_FAILED (500); não reverter criação

assumptions:
  - serviço de e-mail está disponível como dependência injetável no contexto da aplicação
  - message broker está configurado e acessível no ambiente de execução
  - banco de dados possui índice único em users.email (normalizado)
```

---

**documentacao.md**

```md
# Criar Usuário

## Descrição
Endpoint público para cadastro de novos usuários. Recebe nome, e-mail e senha, valida os dados, persiste o usuário com status `inactive` e dispara um e-mail de confirmação de forma assíncrona. Não é idempotente.

---

## Checklist de Implementação

### Validação de entrada
- [ ] Rejeitar campos ausentes ou nulos (`name`, `email`, `password`)
- [ ] Aplicar trim em `name` antes de validar comprimento
- [ ] Validar comprimento do `name` (2–100 chars após trim)
- [ ] Validar formato de e-mail (RFC 5322) — retornar `INVALID_INPUT` (422) se inválido
- [ ] Validar presença e tipo de `password` — retornar `INVALID_INPUT` (422) se ausente ou inválido
- [ ] Validar força da senha: 8–72 chars, ao menos 1 maiúscula, 1 número, 1 especial — retornar `WEAK_PASSWORD` (422) se falhar
- [ ] **Nota:** `INVALID_INPUT` cobre ausência/formato incorreto; `WEAK_PASSWORD` cobre exclusivamente força da senha

### Normalização e unicidade
- [ ] Normalizar `email` para lowercase após validação de formato
- [ ] Verificar unicidade do `email` normalizado na base — retornar `EMAIL_ALREADY_EXISTS` (409) se já existe

### Regras de negócio
- [ ] Gerar `id` como UUID v4 na camada de aplicação (não delegar ao banco)
- [ ] Hashear `password` com bcrypt (cost mínimo: 12) antes de salvar
- [ ] Definir `status = inactive` fixo na criação
- [ ] Gerar `created_at` em UTC no momento da persistência

### Persistência
- [ ] Salvar no banco: `id`, `name`, `email`, `password_hash`, `status`, `created_at`
- [ ] **Nunca salvar** `password` em texto puro — somente `password_hash`

### Pós-criação
- [ ] Publicar evento `user.created` no message broker (sync) com payload: `id`, `email`, `created_at`
- [ ] Se falhar ao publicar: logar erro, não reverter a criação
- [ ] Disparar e-mail de confirmação de forma **assíncrona** (não bloquear a resposta)
- [ ] Se o e-mail falhar: retornar `EMAIL_DELIVERY_FAILED` (500) — usuário já existe, não reverter

### Resposta
- [ ] Retornar: `id`, `name`, `email`, `status`, `created_at`
- [ ] **Nunca retornar** `password` ou `password_hash` em nenhuma resposta

---

## Input

| Campo      | Tipo   | Obrigatório | Regras                                          |
|------------|--------|-------------|--------------------------------------------------|
| `name`     | string | sim         | 2–100 chars após trim                           |
| `email`    | string | sim         | formato RFC 5322; normalizado para lowercase    |
| `password` | string | sim         | 8–72 chars, 1 maiúscula, 1 número, 1 especial   |

## Output

| Campo        | Tipo   | Descrição                                      |
|--------------|--------|------------------------------------------------|
| `id`         | string | UUID v4 gerado pela aplicação                  |
| `name`       | string | Nome do usuário (após trim)                    |
| `email`      | string | E-mail normalizado para lowercase              |
| `status`     | enum   | Sempre `inactive` na criação                   |
| `created_at` | string | Timestamp UTC no formato `YYYY-MM-DDTHH:mm:ssZ`|

## Errors

| Código                  | HTTP | Quando ocorre                                                         |
|-------------------------|------|-----------------------------------------------------------------------|
| `INVALID_INPUT`         | 422  | Campo ausente, nulo ou com formato/tamanho inválido                   |
| `WEAK_PASSWORD`         | 422  | Senha sem maiúscula, número ou caractere especial, ou fora de 8–72 chars |
| `EMAIL_ALREADY_EXISTS`  | 409  | E-mail normalizado já cadastrado na base                              |
| `EMAIL_DELIVERY_FAILED` | 500  | Usuário criado, mas falha no envio do e-mail de confirmação           |

---

## Fluxo

1. Receber `name`, `email`, `password` no body da requisição
2. Validar presença e formato de todos os campos — retornar `INVALID_INPUT` (422) se falhar
3. Validar força da senha — retornar `WEAK_PASSWORD` (422) se não atender ao padrão
4. Normalizar `email` para lowercase
5. Verificar unicidade do `email` normalizado — retornar `EMAIL_ALREADY_EXISTS` (409) se já existe
6. Hashear `password` com bcrypt (cost 12)
7. Persistir usuário: `id` (UUID v4), `name`, `email`, `password_hash`, `status = inactive`, `created_at` (UTC)
8. Publicar evento `user.created` no broker com `id`, `email`, `created_at`
9. Disparar e-mail de confirmação de forma assíncrona — retornar `EMAIL_DELIVERY_FAILED` (500) se falhar
10. Retornar os dados do usuário criado (sem senha)

---

## Suposições
- O serviço de e-mail é uma dependência injetável já configurada no ambiente
- O message broker está configurado e acessível no ambiente de execução
- O banco possui índice único em `users.email` (normalizado)
```
