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
- [ ] DTOs
- [ ] Service Layer
- [ ] Tratamento global de exceções
- [ ] Validações
- [ ] Response Pattern
- [ ] Logs estruturados

---

## Fase 3 — Autenticação
- [ ] JWT Authentication
- [ ] Login e registro
- [ ] Roles e permissões

---

## Fase 4 — Geolocalização
- [ ] Geospatial Queries MongoDB
- [ ] Busca de motoristas próximos
- [ ] Atualização de localização

---

## Fase 5 — Tempo real
- [ ] WebSocket
- [ ] Rastreamento em tempo real
- [ ] Eventos de corrida

---

## Fase 6 — Arquitetura avançada
- [ ] Docker
- [ ] Docker Compose
- [ ] Observabilidade
- [ ] Testes automatizados
- [ ] CI/CD
- [ ] Deploy

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
# Licença

Projeto desenvolvido para fins de estudo e portfólio.
