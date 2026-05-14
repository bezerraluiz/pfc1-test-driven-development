# TuiAnimator — Ciclo 7

## Descrição

Exibe um spinner animado com frames Braille em uma thread de background enquanto o cálculo é processado. O `PrintStream` é injetado via construtor para permitir testes sem `System.out`. Ao parar, limpa a linha do terminal com código ANSI.

---

## Checklist de Implementação

### Estrutura
- [x] Criar `src/main/java/com/pfc/tdd/calculator/tui/TuiAnimator.java`
- [x] Exatamente 2 campos de instância: `String mensagem` e `PrintStream saida` (OC #8)
- [x] Thread como variável local em `start()` — não como campo da classe

### Construtor
- [x] `public TuiAnimator(String mensagem, PrintStream saida)`
- [x] Atribuir aos campos finais

### Método start()
- [x] Criar `Thread` com `Runnable` que itera os frames
- [x] Frames Braille: `{"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"}`
- [x] Loop: `while (!Thread.currentThread().isInterrupted())`
- [x] Imprimir: `saida.print("\r" + frame + " " + mensagem)`
- [x] Dormir 80ms entre frames
- [x] Capturar `InterruptedException`: chamar `Thread.currentThread().interrupt()` e sair do loop

### Método stop()
- [x] Chamar `thread.interrupt()`
- [x] Aguardar com `thread.join(200)` (timeout 200ms)
- [x] Limpar linha: `saida.print("\033[2K\r")`
- [x] Chamar `saida.flush()`

---

## Frames

| Posição | Frame |
|---------|-------|
| 0       | ⠋     |
| 1       | ⠙     |
| 2       | ⠹     |
| 3       | ⠸     |
| 4       | ⠼     |
| 5       | ⠴     |
| 6       | ⠦     |
| 7       | ⠧     |
| 8       | ⠇     |
| 9       | ⠏     |

---

## Input (construtor)

| Parâmetro  | Tipo        | Descrição                               |
|------------|-------------|-----------------------------------------|
| `mensagem` | String      | Texto exibido ao lado do spinner        |
| `saida`    | PrintStream | Stream de saída (injetar para testar)   |

---

## Regras

- **Nunca usar `System.out` diretamente** — sempre injetar `PrintStream`.
- Loop usa `isInterrupted()`, não `AtomicBoolean` como campo.
- `InterruptedException` deve relançar o interrupt: `Thread.currentThread().interrupt()`.
- `stop()` sempre faz `flush()` após limpar a linha.
- Classe com no máximo 30 linhas (OC #7).

---

## Abordagem de Teste

- Criar `ByteArrayOutputStream` e embrulhar como `PrintStream`.
- Passar como `saida` no construtor.
- Chamar `start()`, aguardar alguns ms, chamar `stop()`.
- Verificar que `stop()` não lança exceção e que a stream foi escrita.

---

## Princípios Object Calisthenics aplicados

| Regra | Aplicação |
|-------|-----------|
| OC #7 | Classe ≤ 30 linhas |
| OC #8 | Exatamente 2 campos: `mensagem`, `saida` |
| KISS  | Thread simples sem ExecutorService |
| YAGNI | Sem suporte a múltiplos spinners simultâneos |

---

## Suposições

- `start()` sempre é chamado antes de `stop()`.
- Terminal suporta carriage return (`\r`) para reposição do cursor na mesma linha.
