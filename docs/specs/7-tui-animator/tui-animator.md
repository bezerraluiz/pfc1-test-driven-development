# TuiAnimator — Ciclo TDD 7

## Descrição

Exibe um spinner Braille animado no terminal enquanto o cálculo processa. Executa em uma thread de background iniciada por `start()` e interrompida por `stop()`. Tem exatamente 2 campos de instância: a mensagem exibida e o `PrintStream` de saída.

---

## Checklist de Implementação

### Estrutura da classe
- [x] Criar `src/test/java/com/pfc/tdd/calculator/tui/TuiAnimatorTest.java` (RED) — package corrigido para `calculator` (typo no doc)
- [x] Criar `src/main/java/com/pfc/tdd/calculator/tui/TuiAnimator.java` (GREEN)
- [x] 2 campos finais: `private final String mensagem` e `private final PrintStream saida`; `thread` é 3° campo mutable (gap: stop() precisa da referência — OC #8 não pode ser satisfeito estritamente)
- [x] `AtomicBoolean` eliminado — loop usa `!Thread.currentThread().isInterrupted()` (equivalente funcional)
- [x] Classe com **no máximo 30 linhas** de código

### Método start()
- [x] Criar `Thread` com `Runnable` inline que itera os frames
- [x] Loop do Runnable: `while (!isInterrupted())` → imprimir `"\r" + frame + " " + mensagem` → `Thread.sleep(80)`
- [x] Capturar `InterruptedException` no Runnable: chamar `Thread.currentThread().interrupt()` e sair do loop
- [x] Iniciar a thread como daemon e retornar imediatamente

### Método stop()
- [x] Chamar `thread.interrupt()`
- [x] Aguardar com `thread.join(200)` (timeout de 200ms)
- [x] Limpar a linha: `saida.print("\033[2K\r")` e `saida.flush()`

### Frames do spinner
```
⠋ ⠙ ⠹ ⠸ ⠼ ⠴ ⠦ ⠧ ⠇ ⠏
```
Definir como constante estática: `private static final String[] FRAMES = {"⠋","⠙","⠹","⠸","⠼","⠴","⠦","⠧","⠇","⠏"};`

---

## Input (Construtor)

| Parâmetro  | Tipo        | Descrição                                                         |
|------------|-------------|-------------------------------------------------------------------|
| `mensagem` | String      | Texto ao lado do spinner (ex: `"Calculando..."`)                 |
| `saida`    | PrintStream | Stream de destino; usar `System.out` em produção                 |

## Métodos

| Método    | Retorno | Descrição                                                         |
|-----------|---------|-------------------------------------------------------------------|
| `start()` | void    | Inicia a animação em thread de background; retorna imediatamente  |
| `stop()`  | void    | Para a thread, limpa a linha e aguarda finalização (até 200ms)   |

---

## Casos de Teste

> Nos testes, **nunca usar `System.out`** — injetar `PrintStream` sobre `ByteArrayOutputStream`.

```java
ByteArrayOutputStream baos = new ByteArrayOutputStream();
PrintStream ps = new PrintStream(baos);
TuiAnimator animator = new TuiAnimator("Calculando...", ps);
```

| Cenário                              | Asserção                                    |
|--------------------------------------|---------------------------------------------|
| Após `start()` + 200ms + `stop()`   | Output contém `"Calculando..."`             |
| Após `stop()`                        | Output contém `"\033[2K"` (linha limpa)     |
| Após `stop()`                        | Nenhum novo output gerado (thread terminou) |

---

## Regras

- `Thread` direta — **não usar `ExecutorService`** (KISS).
- `AtomicBoolean rodando` é **local a `start()`**, não campo da classe.
- `stop()` deve ser seguro para chamar múltiplas vezes — se a thread já terminou, `join` retorna imediatamente.
- O `\r` sobrescreve a linha atual sem criar nova linha — o terminal deve suportar ANSI.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #8 | Exatamente 2 campos: `mensagem` + `saida` — thread e AtomicBoolean são locais |
| OC #7 | Classe ≤ 30 linhas totais |
| KISS  | `Thread` direta, sem `ExecutorService` |
| YAGNI | Frames não são configuráveis via construtor — nenhum teste pede isso |

---

## Suposições

- `TuiApp` pula a animação quando `saida != System.out` — os testes de `TuiApp` não precisam lidar com timing de threads.
- `Thread.sleep(80)` pode ser ajustado se os testes forem flaky — usar timeout de `200ms` no join garante margem suficiente.
