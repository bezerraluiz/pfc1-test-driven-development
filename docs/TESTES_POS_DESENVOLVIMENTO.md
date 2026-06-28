# Testes Pós-Desenvolvimento — Branch `without-tdd`

> Data de execução: 2026-06-28
> Branch: `without-tdd`
> Resultado geral: **29 testes, 0 falhas, 0 erros**

---

## O que é esse documento?

Na branch `without-tdd`, o código foi escrito **primeiro**, sem nenhum teste guiando o desenvolvimento. Isso é o oposto do TDD (Test-Driven Development), onde os testes vêm antes do código.

Depois que o código estava pronto, escrevemos testes para verificar se ele funciona corretamente. Esse documento explica:

- Quais classes foram testadas
- Quais cenários cada teste cobre
- Por que cada teste importa
- O resultado de cada grupo de testes

---

## Estrutura da aplicação (resumo)

A calculadora de salário líquido tem duas camadas:

```
Entrada do usuário (texto)
        ↓
   InputParser          ← valida e converte o texto em GrossSalary
        ↓
SalaryNetCalculator     ← orquestra o cálculo
        ↓
  InssTable             ← calcula o desconto de INSS progressivo
  IrrfTable             ← calcula o desconto de IRRF progressivo
        ↓
 CalculationResult      ← resultado final (bruto, INSS, IRRF, líquido)
```

---

## Grupo 1 — `InssTableTest` (5 testes)

**Arquivo:** `src/test/java/com/pfc/tdd/calculator/domain/InssTableTest.java`
**Classe testada:** `InssTable`
**O que ela faz:** Calcula o desconto do INSS usando a tabela progressiva de 2026.

### O que é cálculo progressivo?

É como funciona o imposto de renda: você não paga a mesma alíquota sobre tudo. Cada pedaço do salário paga uma alíquota diferente conforme a faixa. Por exemplo:
- Até R$ 1.621,00 → 7,5%
- De R$ 1.621,00 até R$ 2.902,84 → 9%
- De R$ 2.902,84 até R$ 4.354,27 → 12%
- De R$ 4.354,27 até R$ 8.475,55 → 14%

### Testes e por que foram escritos

| Teste | Entrada | Esperado | Motivo |
|---|---|---|---|
| `salarioDentroFaixa1_deveAplicarSomente7Porcento` | R$ 1.500,00 | R$ 112,50 | Confirmar que apenas a faixa 1 é usada quando o salário não ultrapassa seu limite |
| `salarioNoTetoFaixa1_deveCalcularCorretamente` | R$ 1.621,00 | R$ 121,58 | Testar o valor exato de fronteira da faixa 1 — erros costumam aparecer nos limites |
| `salarioNaFaixa3_deveAplicarFaixasProgressivas` | R$ 3.000,00 | R$ 248,60 | Verificar que as 3 primeiras faixas são somadas corretamente |
| `salarioNaFaixa4_deveAplicarTodasAsFaixas` | R$ 5.000,00 | R$ 501,51 | Garantir que as 4 faixas funcionam juntas |
| `salarioNoTeto_deveCalcularValorMaximoDeInss` | R$ 8.475,55 | R$ 988,09 | Verificar o valor máximo possível de INSS (teto da tabela 2026) |

### Resultado

```
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```

Todos aprovados. O cálculo progressivo está correto em todas as faixas.

---

## Grupo 2 — `IrrfTableTest` (5 testes)

**Arquivo:** `src/test/java/com/pfc/tdd/calculator/domain/IrrfTableTest.java`
**Classe testada:** `IrrfTable`
**O que ela faz:** Calcula o IRRF (Imposto de Renda Retido na Fonte) usando a tabela de 05/2025.

> **Detalhe importante:** O IRRF é calculado sobre a **base tributável**, que é `salário bruto − INSS`. Não é sobre o bruto diretamente.

### A tabela de isenção

Quem recebe até R$ 2.428,80 (após descontar o INSS) é isento de IRRF — paga R$ 0,00.

### Testes e por que foram escritos

| Teste | Entrada (base tributável) | Esperado | Motivo |
|---|---|---|---|
| `baseAbaixoDaIsencao_deveRetornarZero` | R$ 1.500,00 | R$ 0,00 | Quem está abaixo da isenção não pode pagar IRRF |
| `baseLimiteExatoDaIsencao_deveRetornarZero` | R$ 2.428,80 | R$ 0,00 | Testar a fronteira exata — quem está exatamente no limite ainda é isento |
| `baseNaFaixa3_deveCalcularIrrf` | R$ 3.500,00 | R$ 130,84 | Verificar a fórmula `base × 15% − R$ 394,16` da faixa 3 |
| `baseNaFaixa4_deveCalcularIrrf` | R$ 4.490,40 | R$ 334,85 | Verificar a fórmula da faixa 4 (`22,5% − R$ 675,49`) |
| `baseMuitoAlta_naoDeveRetornarNegativo` | R$ 10.000,00 | > R$ 0,00 | Garantir que o IRRF nunca seja negativo (proteção defensiva) |

### Resultado

```
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
```

Todos aprovados. A isenção, as fórmulas por faixa e a proteção contra valor negativo estão corretas.

---

## Grupo 3 — `GrossSalaryTest` (3 testes)

**Arquivo:** `src/test/java/com/pfc/tdd/calculator/domain/GrossSalaryTest.java`
**Classe testada:** `GrossSalary` (um `record` Java — objeto imutável)
**O que ela faz:** Representa o salário bruto e valida que o valor é positivo.

### Testes e por que foram escritos

| Teste | Entrada | Comportamento esperado | Motivo |
|---|---|---|---|
| `valorPositivo_deveCriarObjeto` | R$ 3.000,00 | Cria o objeto sem erro | Caminho feliz — o caso normal deve funcionar |
| `valorZero_deveLancarExcecao` | R$ 0,00 | Lança `IllegalArgumentException` | Salário zero não faz sentido e deve ser rejeitado |
| `valorNegativo_deveLancarExcecao` | R$ -100,00 | Lança `IllegalArgumentException` | Salário negativo é fisicamente impossível |

### Resultado

```
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
```

Todos aprovados. A validação do salário bruto funciona corretamente.

---

## Grupo 4 — `SalaryNetCalculatorTest` (4 testes)

**Arquivo:** `src/test/java/com/pfc/tdd/calculator/domain/SalaryNetCalculatorTest.java`
**Classe testada:** `SalaryNetCalculator`
**O que ela faz:** Orquestra o cálculo completo — usa `InssTable` e `IrrfTable` juntas e retorna o `CalculationResult` com bruto, INSS, IRRF e líquido.

> Esta é a classe mais importante do ponto de vista de integração. Um erro aqui poderia aparecer mesmo com `InssTable` e `IrrfTable` corretas individualmente — por exemplo, se o cálculo do IRRF usasse o bruto direto em vez da base tributável.

### Testes e por que foram escritos

| Teste | Entrada | O que verifica | Motivo |
|---|---|---|---|
| `salarioIsento_deveCalcularApenasInss` | R$ 1.500,00 | INSS = R$ 112,50, IRRF = R$ 0,00, líquido = R$ 1.387,50 | Caso simples onde só existe desconto de INSS |
| `salarioComInssEIrrf_deveCalcularTodosOsDescontos` | R$ 5.000,00 | INSS correto, IRRF > 0, líquido = bruto − INSS − IRRF | Caso completo com os dois descontos |
| `resultadoNaoDevolveValorLiquidoNegativo` | R$ 1.000,00 | líquido > R$ 0,00 | Garantia de sanidade — o salário líquido nunca pode ser negativo |
| `salarioComVirgula_devePreservarValores` | R$ 3.000,50 | líquido = bruto − INSS − IRRF | Verificar consistência interna com centavos no salário |

### Resultado

```
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
```

Todos aprovados. O fluxo de orquestração e a ordem dos cálculos (bruto → INSS → base tributável → IRRF → líquido) estão corretos.

---

## Grupo 5 — `InputParserTest` (12 testes)

**Arquivo:** `src/test/java/com/pfc/tdd/calculator/tui/InputParserTest.java`
**Classe testada:** `InputParser`
**O que ela faz:** Recebe o texto que o usuário digitou no terminal e converte para um `GrossSalary`. Também valida se o texto é um número válido e positivo.

> Esta é a classe com mais testes porque é a **porta de entrada do sistema**. Ela lida com texto livre do usuário, que pode ter qualquer formato — e por isso tem mais casos de erro para cobrir.

### Testes do caminho feliz (entradas válidas)

| Teste | Entrada | Esperado | Motivo |
|---|---|---|---|
| `entradaValida_deveRetornarGrossSalary` | `"3000"` | GrossSalary com valor 3000 | Caso mais simples possível |
| `entradaComVirgula_deveAceitarComoDecimal` | `"3000,50"` | GrossSalary com valor 3000.50 | No Brasil usamos vírgula como separador decimal — o parser deve aceitar |
| `entradaComPonto_deveAceitarComoDecimal` | `"3000.50"` | GrossSalary com valor 3000.50 | Formato internacional também deve funcionar |

### Testes de entradas inválidas

| Teste | Entrada | Comportamento esperado | Motivo |
|---|---|---|---|
| `entradaNula_deveLancarInvalidInputException` | `null` | Lança `InvalidInputException` | `null` não deve causar `NullPointerException` — precisa de mensagem amigável |
| `entradaVazia_deveLancarInvalidInputException` | `""` | Lança `InvalidInputException` | String vazia não é um número |
| `entradaEmBranco_deveLancarInvalidInputException` | `"   "` | Lança `InvalidInputException` | Espaços em branco são tratados como vazio |
| `entradaComTexto_deveLancarInvalidInputException` | `"abc"` | Lança `InvalidInputException` | Texto não é número |
| `entradaComSimbolo_deveLancarInvalidInputException` | `"R$3000"` | Lança `InvalidInputException` | Usuário pode tentar digitar com símbolo de real |
| `entradaZero_deveLancarInvalidInputException` | `"0"` | Lança `InvalidInputException` | Zero é número válido, mas salário zero não faz sentido |
| `entradaNegativa_deveLancarInvalidInputException` | `"-500"` | Lança `InvalidInputException` | Número negativo é sintaticamente válido, mas semanticamente inválido |

### Testes das mensagens de erro

| Teste | O que verifica | Motivo |
|---|---|---|
| `mensagemDeErroParaEntradaInvalida_deveEstarEmPortugues` | A mensagem contém "inválida" ou "válido" | A aplicação é em português — mensagens em inglês confundem o usuário |
| `mensagemDeErroParaSalarioZero_deveEstarEmPortugues` | A mensagem contém "maior" ou "zero" | Mensagem específica para salário zero, diferente da mensagem de formato inválido |

### Resultado

```
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
```

Todos aprovados. O parser aceita formatos com vírgula e ponto, rejeita todas as entradas inválidas com `InvalidInputException` e retorna mensagens em português.

---

## Resultado final

```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.pfc.tdd.calculator.tui.InputParserTest
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

Running com.pfc.tdd.calculator.domain.SalaryNetCalculatorTest
Tests run: 4,  Failures: 0, Errors: 0, Skipped: 0

Running com.pfc.tdd.calculator.domain.IrrfTableTest
Tests run: 5,  Failures: 0, Errors: 0, Skipped: 0

Running com.pfc.tdd.calculator.domain.GrossSalaryTest
Tests run: 3,  Failures: 0, Errors: 0, Skipped: 0

Running com.pfc.tdd.calculator.domain.InssTableTest
Tests run: 5,  Failures: 0, Errors: 0, Skipped: 0

Tests run: 29, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
```

---

## O que esses resultados dizem sobre a abordagem sem TDD?

O fato de todos os 29 testes passarem na primeira execução pode parecer positivo, mas é importante entender o **contexto metodológico**:

1. **Os testes foram escritos DEPOIS do código.** Isso significa que, inconscientemente, quem escreveu os testes já sabia o que o código faz — e pode ter escrito testes que confirmam o comportamento atual em vez de questionar se ele está correto.

2. **Sem TDD, o design é guiado pela implementação.** Com TDD, cada teste força o desenvolvedor a pensar na interface pública da classe antes de implementá-la. Sem esse processo, o código pode ter sido escrito de uma forma que funciona, mas que é mais difícil de testar ou manter.

3. **Os erros encontrados durante o desenvolvimento não são rastreáveis.** No TDD, cada teste que falha é um passo documentado no processo. Aqui, os erros que aconteceram enquanto o código era escrito foram corrigidos manualmente, sem registro.

4. **Comparação com a branch `with-tdd`:** Na branch onde TDD foi aplicado, os testes existiram antes do código. Isso garante que cada teste representa um requisito real, não uma confirmação do comportamento já implementado.

> Este documento faz parte de um estudo comparativo entre desenvolvimento com e sem TDD. Os dados de cobertura, tempo de desenvolvimento e quantidade de retrabalho estão registrados em `docs/METRICAS_SEM_TDD_20260514.md`.
