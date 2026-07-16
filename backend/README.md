# 🚗 PGE-Ride - Sistema de Mobilidade PGE-Ceará (Backend)

O backend do **PGE-Ride** é uma arquitetura moderna baseada em microsserviços desenvolvida com o ecossistema Spring Boot, Spring Cloud, mensageria via RabbitMQ e persistência em bancos de dados relacionais e em memória. O sistema foi projetado para ser resiliente, escalável e de fácil monitoramento.

---

## 🛠️ Tecnologias e Bibliotecas Utilizadas

*   **Java 17 / Spring Boot 3.x** - Framework principal de desenvolvimento.
*   **Spring Cloud Netflix Eureka** - Service Registry para descoberta dinâmica de instâncias.
*   **Spring Cloud Gateway** - API Gateway para roteamento centralizado, controle de segurança e prevenção de CORS.
*   **Spring Data JPA** - Abstração de banco de dados para integração com Postgres.
*   **Spring AMQP** - Integração de microsserviços via mensageria assíncrona usando RabbitMQ.
*   **PostgreSQL** - Banco de dados relacional para persistência de contas e transações de corridas.
*   **Redis** - Armazenamento de alta performance para controle de sessões e cache rápido.
*   **Lombok** - Redução de código boilerplate (Getters, Setters, Builders).

---

## 🏗️ Arquitetura e Portas Consumidas

Cada serviço desempenha um papel único dentro do ecossistema e se comunica de forma síncrona (via REST/OpenFeign) ou assíncrona (via RabbitMQ):

| Serviço | Porta | Descrição |
| :--- | :--- | :--- |
| `eureka-server` | `8761` | Servidor de Registro e Descoberta de Serviços. |
| `gateway` | `8080` | Ponto único de entrada do frontend. Roteia chamadas para os serviços internos. |
| `account-service` | `8081` | Gerenciamento de usuários (Passageiros e Motoristas). |
| `ride-service` | `8082` | Gerenciamento das transações de corrida e SSE (Server-Sent Events). |
| `PostgreSQL` | `5432` | Banco de dados central do sistema (contém múltiplos schemas/bancos). |
| `RabbitMQ` | `5672` / `15672` | Broker de Mensageria para eventos assíncronos. |

---

## 🔑 Credenciais do RabbitMQ
A fila de mensageria sobe automaticamente via container Docker.

*   **Host:** `localhost`
*   **Porta da Aplicação (AMQP):** `5672`
*   **Porta do Painel de Controle (Dashboard):** `15672`
*   **Usuário Administrador:** `guest`
*   **Senha:** `guest`
*   **Dashboard URL:** [http://localhost:15672](http://localhost:15672)

---

## 📈 Fluxo de Mensagens e Notificações (RabbitMQ)

O RabbitMQ gerencia a reatividade do sistema de forma assíncrona. O fluxo de disparo funciona da seguinte maneira:

1.  **Criação de Corrida:** Quando o Passageiro solicita uma corrida no frontend, uma chamada HTTP POST atinge o `/api/rides`. O `ride-service` salva o registro no banco com status `SOLICITADO` e publica uma mensagem na exchange `ride.exchange` com a routing key `ride.requested`.
2.  **Fila de Notificação de Motoristas:** A fila `driver-notification-queue` consome essa mensagem. O sistema pode enviar alertas ou atualizar painéis de motoristas próximos.
3.  **Aceitação de Corrida:** Quando um motorista aceita a corrida no painel, o `ride-service` atualiza o registro com status `ACEITO` e dispara um evento na routing key `ride.accepted`. 
4.  **Disparo do SSE:** O listener do `ride-service` consome o evento `ride.accepted` e canaliza a atualização instantaneamente para o endpoint de **Server-Sent Events (SSE)**, notificando o navegador do passageiro em tempo real.

---

## 🛣️ Endpoints Principais (Consumidos via API Gateway - `8080`)

### 👥 Account Service (`/api/accounts`)
*   `GET /api/accounts` - Lista todos os usuários cadastrados.
*   `GET /api/accounts/{id}` - Busca os detalhes de um usuário específico.
*   `POST /api/accounts` - Cadastra um novo usuário (Passageiro ou Motorista).

### 🚗 Ride Service (`/api/rides`)
*   `POST /api/rides` - Cria uma nova solicitação de corrida.
*   `GET /api/rides/available` - Lista todas as corridas ativas com status `SOLICITADO` (Exclusivo para motoristas).
*   `PUT /api/rides/{rideId}/accept` - Aceita uma corrida pendente. Espera um JSON Body `{ "driverId": X }`.
*   `GET /api/rides/stream/{passengerId}` - Endpoint de Server-Sent Events (SSE) para atualização em tempo real do passageiro.

---

## 🚀 Como Subir o Backend

### Pré-requisitos
*   Docker e Docker Compose instalados.
*   JDK 17 configurado.
*   Maven instalado.

### Passo a Passo

1.  **Iniciar Infraestrutura (Docker):**
    Na raiz do projeto backend (onde fica o arquivo `docker-compose.yml`), execute no terminal:
    ```bash
    docker compose up -d
    ```
    *(Isso iniciará o PostgreSQL, Redis e RabbitMQ com as portas devidamente expostas).*

2.  **Executar Serviços via Script (Batch/Bash):**
    Execute o script automatizado fornecido na raiz para compilar e subir todos os microsserviços na ordem correta de dependência:
    ```bash
    ./start-backend.bat
    # ou se estiver no Linux/macOS
    chmod +x start-backend.sh && ./start-backend.sh
    ```

3.  **Acompanhar logs:**
    Você pode monitorar o terminal de cada serviço para garantir que todos se registraram com sucesso no painel do Eureka em: [http://localhost:8761](http://localhost:8761).