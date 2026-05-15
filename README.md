# 🚗 RideFlow API

> Backend REST API for urban mobility, inspired by Uber — built with Java 21, Spring Boot 3, and MongoDB.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.x-brightgreen)](https://www.mongodb.com/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## ✨ Features

| Capability | Technology |
|---|---|
| JWT Authentication (access + refresh tokens) | Spring Security + JJWT |
| Real-time driver tracking | WebSocket (STOMP) |
| Geospatial queries — nearby drivers | MongoDB 2dsphere index |
| Full ride lifecycle (request → accept → start → complete) | Spring Data MongoDB |
| Role-based access control (PASSENGER / DRIVER / ADMIN) | `@PreAuthorize` |
| Paginated ride history | Spring Data Pageable |
| Structured error responses | RFC 9457 ProblemDetail |

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────┐
│                        Client App                        │
│              (mobile / web / Postman / wscat)            │
└────────────────────────┬────────────────┬────────────────┘
                         │ HTTP REST      │ WebSocket STOMP
                         ▼                ▼
┌──────────────────────────────────────────────────────────┐
│                   Spring Boot 3 App                      │
│  ┌─────────────┐  ┌────────────────┐  ┌───────────────┐ │
│  │ Controllers │  │ WS Handler     │  │ Security      │ │
│  │  /api/v1/*  │  │ /app/driver.*  │  │ JWT Filter    │ │
│  └──────┬──────┘  └───────┬────────┘  └───────────────┘ │
│         │                 │                              │
│  ┌──────▼─────────────────▼──────────────────────────┐  │
│  │                    Services                        │  │
│  │  AuthService │ RideService │ DriverService         │  │
│  └──────────────────────────┬───────────────────────-─┘  │
│                             │                            │
│  ┌──────────────────────────▼───────────────────────-─┐  │
│  │               Repositories (Spring Data)           │  │
│  └──────────────────────────┬───────────────────────-─┘  │
└─────────────────────────────┼────────────────────────────┘
                              │
                    ┌─────────▼──────────┐
                    │      MongoDB        │
                    │  users / drivers /  │
                    │  rides collections  │
                    │  2dsphere geo index │
                    └─────────────────────┘
```

---

## 🗂️ Project Structure

```
src/main/java/com/rideflow/
├── config/
│   ├── JwtProperties.java       # @ConfigurationProperties for JWT settings
│   ├── MongoConfig.java         # Enables MongoDB auditing
│   ├── SecurityConfig.java      # JWT filter chain, RBAC rules
│   └── WebSocketConfig.java     # STOMP broker, /ws endpoint
├── controller/
│   ├── AuthController.java      # POST /auth/register, /login, /refresh
│   ├── DriverController.java    # Driver profile + nearby search
│   └── RideController.java      # Full ride lifecycle
├── dto/
│   ├── request/                 # Validated input records
│   └── response/                # Output records (no entity leakage)
├── exception/
│   ├── GlobalExceptionHandler.java  # RFC 9457 ProblemDetail responses
│   ├── BusinessException.java
│   └── ResourceNotFoundException.java
├── model/
│   ├── User.java                # @Document — users collection
│   ├── Driver.java              # @GeoSpatialIndexed location field
│   ├── Ride.java                # Full ride with status + timestamps
│   ├── GeoJsonPoint.java        # { type:"Point", coordinates:[lng,lat] }
│   ├── Role.java                # PASSENGER | DRIVER | ADMIN
│   ├── DriverStatus.java        # OFFLINE | AVAILABLE | ON_RIDE
│   └── RideStatus.java          # REQUESTED → ACCEPTED → IN_PROGRESS → COMPLETED
├── repository/
│   ├── UserRepository.java
│   ├── DriverRepository.java    # findByLocationNearAndStatus (geo query)
│   └── RideRepository.java
├── security/
│   ├── JwtService.java          # Token generation & validation
│   ├── JwtAuthenticationFilter.java
│   └── UserDetailsServiceImpl.java
├── service/
│   ├── AuthService.java
│   ├── DriverService.java
│   └── RideService.java
└── websocket/
    └── LocationWebSocketHandler.java  # @MessageMapping("/driver.location")
```

---

## 🚀 Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- MongoDB running on `localhost:27017`

### Running locally

```bash
# 1. Clone
git clone https://github.com/joaodddev/rideflowapi.git
cd rideflowapi

# 2. Start MongoDB (if not running)
mongod --dbpath /data/db

# 3. Run
./mvnw spring-boot:run

# Or with custom settings
MONGODB_URI=mongodb://localhost:27017/rideflowdb \
JWT_SECRET=your-32-char-secret-here \
./mvnw spring-boot:run
```

The API starts on **http://localhost:8080**

---

## 📡 REST API Reference

### Authentication

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/auth/register` | ❌ | Register as PASSENGER or DRIVER |
| POST | `/api/v1/auth/login` | ❌ | Login — returns JWT tokens |
| POST | `/api/v1/auth/refresh` | ❌ | Refresh access token |

**Register body:**
```json
{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123",
  "phone": "+5511999999999",
  "role": "PASSENGER"
}
```

**Login response:**
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci...",
  "tokenType": "Bearer",
  "userId": "665f...",
  "email": "joao@example.com",
  "name": "João Silva",
  "role": "PASSENGER"
}
```

---

### Rides

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/rides` | PASSENGER | Request a new ride |
| DELETE | `/api/v1/rides/{id}/cancel` | PASSENGER | Cancel a ride |
| GET | `/api/v1/rides/my-history` | PASSENGER | Paginated ride history |
| PUT | `/api/v1/rides/{id}/accept` | DRIVER | Accept a ride request |
| PUT | `/api/v1/rides/{id}/start` | DRIVER | Start the ride |
| PUT | `/api/v1/rides/{id}/complete?distanceKm=12.5` | DRIVER | Complete ride + calc fare |
| GET | `/api/v1/rides/driver-history` | DRIVER | Driver's ride history |
| GET | `/api/v1/rides/{id}` | ANY | Get ride details |

**Request ride body:**
```json
{
  "originLat": -23.5505,
  "originLng": -46.6333,
  "originAddress": "Av. Paulista, 1000 - São Paulo",
  "destinationLat": -23.5615,
  "destinationLng": -46.6560,
  "destinationAddress": "Pinheiros, São Paulo"
}
```

---

### Drivers

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/drivers/register` | DRIVER | Create driver profile |
| PUT | `/api/v1/drivers/status?status=AVAILABLE` | DRIVER | Go online/offline |
| GET | `/api/v1/drivers/nearby?lat=-23.55&lng=-46.63&radius=5` | ANY | Find nearby drivers |
| GET | `/api/v1/drivers/me` | DRIVER | Own profile |
| GET | `/api/v1/drivers/{id}` | ANY | Driver by ID |

---

## 🔌 WebSocket — Real-time Tracking

Connect via STOMP over SockJS:

```javascript
const socket = new SockJS('http://localhost:8080/ws');
const client = Stomp.over(socket);

client.connect({ Authorization: 'Bearer <token>' }, () => {

  // 🗺️ Passenger: track driver on active ride
  client.subscribe('/topic/ride.RIDE_ID.location', (msg) => {
    const { latitude, longitude, timestamp } = JSON.parse(msg.body);
    updateMapMarker(latitude, longitude);
  });

  // 📋 Track ride status changes
  client.subscribe('/topic/ride.RIDE_ID', (msg) => {
    const ride = JSON.parse(msg.body);
    console.log('Status:', ride.status);
  });

  // 🚗 Driver: send location every 3 seconds
  setInterval(() => {
    navigator.geolocation.getCurrentPosition(pos => {
      client.send('/app/driver.location', {}, JSON.stringify({
        latitude: pos.coords.latitude,
        longitude: pos.coords.longitude,
        rideId: 'RIDE_ID'   // optional
      }));
    });
  }, 3000);
});
```

**WebSocket Topics:**

| Topic | Publisher | Subscribers |
|-------|-----------|-------------|
| `/topic/ride.{rideId}.location` | Driver location updates | Passenger tracking driver |
| `/topic/ride.{rideId}` | Ride status changes | Both parties |
| `/topic/rides.new` | New ride requested | Available drivers |
| `/topic/drivers.location` | All driver positions | Admin dashboards |
| `/user/queue/rides` | Personal ride updates | Logged-in passenger |

---

## 🌍 Geospatial Queries

MongoDB stores driver locations as **GeoJSON Points** with a `2dsphere` index:

```json
{
  "location": {
    "type": "Point",
    "coordinates": [-46.6333, -23.5505]
  }
}
```

> ⚠️ GeoJSON order is **[longitude, latitude]** — opposite of what most humans expect.

Spring Data generates `$nearSphere` queries automatically:

```java
// Repository method — Spring Data builds the geo query
List<Driver> findByLocationNearAndStatus(Point point, Distance distance, DriverStatus status);
```

---

## 🔐 Security

All protected endpoints require:
```
Authorization: Bearer <accessToken>
```

Role enforcement via `@PreAuthorize`:
- `hasRole('PASSENGER')` — ride requests, cancellation
- `hasRole('DRIVER')` — accept, start, complete rides
- `hasRole('ADMIN')` — admin endpoints (`/api/v1/admin/**`)

---

## 💡 Key Technical Decisions

**Why MongoDB?**
- Native GeoJSON + 2dsphere indexes for geospatial queries
- Flexible schema fits the varied ride/driver document shapes
- Scales horizontally for high ride volumes

**Why STOMP over plain WebSocket?**
- Topic-based pub/sub model maps naturally to rides and locations
- SockJS fallback for browser compatibility
- `convertAndSendToUser` for private passenger notifications

**Why Java Records for DTOs?**
- Immutable by default — no accidental mutation between layers
- Zero boilerplate compared to traditional POJOs
- Built-in `equals`, `hashCode`, `toString`

---

## 📄 License

MIT © João — feel free to use this as a portfolio reference or starting point.
