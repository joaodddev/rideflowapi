# RideFlow API 🚖

Backend inspirado em aplicações de mobilidade urbana como Uber, desenvolvido com Java 21, Spring Boot e MongoDB.

O projeto foi criado com foco em:
- arquitetura backend moderna
- APIs REST
- geolocalização
- comunicação em tempo real
- autenticação JWT
- escalabilidade
- boas práticas de engenharia de software

---

# Tecnologias

- Java 21
- Spring Boot 3
- Spring Security
- MongoDB
- Maven
- Lombok
- WebSocket
- Git/GitHub

---

# Objetivo do Projeto

O RideFlow API simula o backend de uma aplicação de mobilidade urbana, permitindo:

- cadastro de motoristas
- gerenciamento de localização
- solicitação de corridas
- rastreamento em tempo real
- matching de motoristas próximos
- autenticação de usuários
- comunicação em tempo real via WebSocket

---

# Funcionalidades Implementadas

## Drivers
- Cadastro de motoristas
- Listagem de motoristas

## Infraestrutura
- Integração com MongoDB
- API REST com Spring Boot
- Configuração inicial do Spring Security

---

# Roadmap do Projeto

## Fase 1 — Estrutura inicial
- [x] Configuração do projeto
- [x] Integração com MongoDB
- [x] CRUD inicial de motoristas
- [x] Configuração do Spring Security

---

## Fase 2 — Arquitetura profissional
- [x] DTOs
- [x] Service Layer
- [x] Tratamento global de exceções
- [x] Validações
- [x] Response Pattern
- [x] Logs estruturados

---

## Fase 3 — Autenticação
- [x] JWT Authentication
- [x] Login e registro
- [x] Roles e permissões

---

## Fase 4 — Geolocalização
- [x] Geospatial Queries MongoDB
- [x] Busca de motoristas próximos
- [x] Atualização de localização

---

## Fase 5 — Tempo real
- [x] WebSocket
- [x] Rastreamento em tempo real
- [x] Eventos de corrida

---

# Estrutura do Projeto

```text
src/main/java/com/rideflow
├── config
├── common
├── driver
└── RideflowApiApplication.java
```

---

# Como Executar o Projeto

## Pré-requisitos

- Java 21
- MongoDB
- Maven

---

## Clonar repositório

```bash
git clone git@github.com:joaodddev/rideflow-api.git
```

---

## Entrar na pasta

```bash
cd rideflow-api
```

---

## Rodar aplicação

```bash
./mvnw spring-boot:run
```

---

# Configuração do MongoDB

Certifique-se que o MongoDB esteja rodando localmente:

```bash
sudo systemctl status mongod
```

A aplicação utiliza:

```yaml
mongodb://localhost:27017/rideflow
```

---

# Endpoints

## Criar motorista

### POST `/drivers`

### Request

```json
{
  "name": "João",
  "email": "joao@email.com",
  "latitude": -22.0,
  "longitude": -47.0,
  "online": true
}
```

---

## Listar motoristas

### GET `/drivers`

---

# Próximos objetivos técnicos

Este projeto será evoluído para incluir:

- Clean Architecture
- DDD
- JWT
- Geolocalização em tempo real
- WebSocket
- Event Driven Architecture
- Observabilidade
- Microsserviços

---

# Autor

João Victor

- [LinkedIn](https://www.linkedin.com/in/joao-victor-macedo-neves/)
- [GitHub](https://github.com/joaodddev)
- [Instagram Tech](https://instagram.com/neeves.dev)
# Licença

Projeto desenvolvido para fins de estudo e portfólio.
