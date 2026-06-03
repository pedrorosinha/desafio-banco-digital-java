```markdown
# Banco Digital API

Uma API RESTful desenvolvida em Java com Spring Boot para simular as operações essenciais de um banco digital. O projeto foca em boas práticas de design de software, consistência de dados através de travas pessimistas (`findByIdWithLock`) para evitar condições de corrida (*race conditions*) e imutabilidade do histórico financeiro.

## Tecnologias Utilizadas

* **Java 25**
* **Spring Boot 3.x**
* **Spring Data JPA** (Persistência de dados)
* **Validation (Bean Validation)** (Validação de contratos de entrada)
* **Lombok** (Produtividade e redução de código boilerplate)
* **Jackson 3 (tools.jackson)** (Serialização/Deserialização de JSON)
* **JUnit 5 & Mockito** (Testes unitários e de integração de fatiamento)
* **Awaitility** (Validação de fluxos assíncronos)

## Diferenciais Técnicos & Arquitetura

1. **Garantia de Concorrência:** Uso de bloqueio pessimista (`@Lock(LockModeType.PESSIMISTIC_WRITE)`) no repositório de contas para assegurar a consistência dos saldos durante transferências simultâneas na mesma conta.
2. **Imutabilidade Financeira:** O endpoint de transações não expõe a operação `PUT`. Uma vez consolidada, uma transação não pode ser alterada, respeitando as regras de auditoria do mercado financeiro real.
3. **Segurança e Validação Estrita:** Configuração customizada do Jackson (`FAIL_ON_UNKNOWN_PROPERTIES`) combinada com um `GlobalExceptionHandler` robusto. A API rejeita propriedades desconhecidas e intrusas nos payloads de entrada com `400 Bad Request`.
4. **Isolamento de DTOs:** DTOs segregados para criação (`ContaRequest`) e atualização parcial (`ContaAtualizarRequest`), impedindo que o saldo seja alterado de forma fraudulenta via `PUT`.
5. **Processamento Assíncrono:** O envio de notificações de transferência ocorre de forma assíncrona (`@Async`), liberando a thread principal da requisição imediatamente após a persistência da transação.

---

## Como Executar o Projeto

### Pré-requisitos
* **JDK 25** instalado.
* **Maven 3.8+** configurado.

### Passos para Instalação e Execução

1. Clone o repositório:
   ```bash
   git clone [https://github.com/pedrorosinha/desafio-banco-digital-java.git](https://github.com/pedrorosinha/desafio-banco-digital-java.git)
   cd desafio-banco-digital-java

```

2. Compile o projeto e baixe as dependências:
```bash
mvn clean compile

```


3. Execute a aplicação:
```bash
mvn spring-boot:run

```



A API estará disponível em `http://localhost:8080`.

---

## Executando os Testes Automatizados

A suíte de testes cobre detalhadamente as regras de negócio da camada Service (incluindo concorrência e fluxos assíncronos) e a integração da camada Controller (testando contratos de payload e tratamento global de exceções do Jackson).

Para rodar todos os testes e gerar o relatório de cobertura, use o comando:

```bash
mvn test

```

---

## Endpoints & Exemplos de Uso

### Contas (`/api/contas`)

#### **Criar uma nova conta**

* **HTTP Method:** `POST`
* **Payload (`POST /api/contas`):**

```json
{
  "nomeTitular": "Pedro Felipe",
  "saldoInicial": 1000.00
}

```

* **Resposta (`201 Created`):**

```json
{
  "id": 1,
  "nomeTitular": "Pedro Felipe",
  "saldo": 1000.00
}

```

#### **Buscar conta por ID**

* **HTTP Method:** `GET`
* **Rota:** `/api/contas/1`
* **Resposta (`200 OK`):**

```json
{
  "id": 1,
  "nomeTitular": "Pedro Felipe",
  "saldo": 1000.00
}

```

#### **Atualizar nome do titular (Seguro)**

* **HTTP Method:** `PUT`
* **Rota:** `/api/contas/1`
* **Payload (`PUT /api/contas/1`):**

```json
{
  "nomeTitular": "Pedro Felipe Rosinha"
}

```

> **Comportamento Estrito:** Se você tentar enviar o campo `saldoInicial` nesta requisição, a API retornará imediatamente um erro `400 Bad Request` informando que o campo não é permitido.

#### **Deletar conta**

* **HTTP Method:** `DELETE`
* **Rota:** `/api/contas/1`
* **Resposta (`204 No Content`)**

---

### Transações (`/api/transacoes`)

#### **Realizar uma transferência**

* **HTTP Method:** `POST`
* **Payload (`POST /api/transacoes`):**

```json
{
  "contaOrigemId": 1,
  "contaDestinoId": 2,
  "valor": 200.00
}

```

* **Resposta (`201 Created`):**

```json
{
  "id": 10,
  "contaOrigemId": 1,
  "contaDestinoId": 2,
  "valor": 200.00,
  "dataHora": "2026-06-03T18:15:30.123Z"
}

```

#### **Buscar comprovante de transação**

* **HTTP Method:** `GET`
* **Rota:** `/api/transacoes/10`
* **Resposta (`200 OK`)**

#### **Remover transação do histórico**

* **HTTP Method:** `DELETE`
* **Rota:** `/api/transacoes/10`
* **Resposta (`204 No Content`)**

---

## Tratamento de Erros (Exemplos)

A API possui um mapeador global de exceções. Abaixo estão alguns exemplos de respostas padronizadas disparadas pela aplicação:

* **Injeção de propriedades não permitidas (Ex: tentar alterar saldo via PUT):**
* **Status:** `400 Bad Request`
* **Body:** `O campo 'saldoInicial' não é permitido nesta requisição.`


* **Saldo Insuficiente:**
* **Status:** `400 Bad Request`
* **Body:** `Saldo insuficiente na conta de origem.`


* **Conta Não Encontrada:**
* **Status:** `404 Not Found`
* **Body:** `Conta de origem não encontrada`



---

## 📂 Estrutura de Pastas Principal

```text
banco_digital
 ├── src/main/java/br/com/dbserver/banco_digital
 │    ├── controllers     # Camada de Entrada HTTP (REST)
 │    ├── dto             # Objetos de Transferência de Dados (Records)
 │    │    ├── conta
 │    │    └── transacao
 │    ├── exception       # GlobalExceptionHandler e Exceções Customizadas
 │    ├── models          # Entidades JPA (Conta, Transacao)
 │    ├── repository      # Interfaces de Acesso ao Banco com travas PESSIMISTIC_WRITE
 │    └── service         # Camada de Regras de Negócio e Serviços Assíncronos
 └── src/test/java/br/com/dbserver/banco_digital
      ├── controllers     # Testes de fatiamento WebMvcTest e validação de JSON
      └── service         # Testes unitários Mockito e validações assíncronas Awaitility

```
