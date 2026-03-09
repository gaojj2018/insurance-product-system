# CODEBUDDY.md

This file provides guidance to CodeBuddy Code when working with code in this repository.

## Project Overview

This is a microservices-based insurance core system built with:
- **Backend**: Java 17 + Spring Boot 3.2.0 + Spring Cloud (Eureka, Gateway, Config)
- **Frontend**: Vue 3 + Element Plus + Vite
- **Database**: MySQL 8.0 with MyBatis Plus ORM
- **Cache**: Redis

## Build and Run Commands

### Backend Services

```bash
# Build a single service
cd <service-directory>
mvn clean package -DskipTests

# Run a service (development)
mvn spring-boot:run

# Run a service with specific profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=node1"

# Run tests
mvn test
```

### Frontend

```bash
cd frontend
npm install        # Install dependencies
npm run dev        # Development server (port 5173)
npm run build      # Production build to dist/
npm run preview    # Preview production build
```

### All Services (Windows)

```batch
start-all-services.bat   # Start all services in separate windows
stop-all-services.bat    # Stop all services
build-frontend.bat       # Build and deploy frontend
```

### Service Startup Order

1. Eureka Server (8761) - Service registry
2. Config Server (8089) - Centralized configuration
3. Gateway (8888) - API gateway
4. Auth Service (8092) - Authentication
5. Business services (Product, Customer, Application, etc.)

## Architecture

```
[Vue3 Frontend] -> [Nginx] -> [Spring Cloud Gateway:8888]
                                    |
        +---------------------------+---------------------------+
        |               |           |           |               |
   [Eureka:8761]   [Config:8089] [Admin:8090] [Auth:8092]   [Business Services]
                                                                   |
                         +-----------------------------------------+
                         |         |         |         |           |
                   [Product] [Customer] [Application] [Underwriting] ...
                         |
                   [MySQL + Redis]
```

## Microservices

| Service | Port | Database | Purpose |
|---------|------|----------|---------|
| eureka-server | 8761 | - | Service Registry |
| gateway | 8888 | - | API Gateway, routing |
| config-service | 8089 | - | Centralized Configuration |
| admin-server | 8090 | - | Spring Boot Admin monitoring |
| auth-service | 8092 | insurance_auth | JWT Authentication |
| backend (product-service) | 8080 | insurance_product | Product, Coverage, Clause |
| customer-service | 8082 | insurance_customer | Customer management |
| application-service | 8083 | insurance_application | Insurance applications |
| underwriting-service | 8084 | insurance_underwriting | Underwriting process |
| policy-service | 8085 | insurance_policy | Policy management |
| claims-service | 8086 | insurance_claims | Claims processing |
| finance-service | 8087 | insurance_finance | Premiums, payments |
| message-service | 8088 | - | Messaging/Notifications |
| report-service | 8091 | - | Reporting |

## Backend Code Structure

Each business service follows a layered architecture:

```
src/main/java/com/insurance/<service>/
├── controller/     # REST endpoints (@RestController)
├── service/        # Business logic
├── repository/     # MyBatis Plus mappers (extends BaseMapper)
├── entity/         # JPA entities with Lombok
└── dto/            # Data Transfer Objects
```

### Entity Pattern

Entities use MyBatis Plus annotations and Lombok:
- `@TableName("table_name")` - Maps to database table
- `@TableId(type = IdType.AUTO)` - Auto-increment primary key
- `@TableField(fill = FieldFill.INSERT)` - Auto-fill on insert
- `@TableLogic` - Logical delete (soft delete)

### API Response Format

All REST endpoints return a consistent response format:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... }
}
```

### Common API Patterns

- `POST /api/<resource>/page` - Paginated query with request body
- `GET /api/<resource>/{id}` - Get by ID
- `POST /api/<resource>` - Create
- `PUT /api/<resource>` - Update
- `DELETE /api/<resource>/{id}` - Delete

## Frontend Structure

```
frontend/src/
├── api/            # Axios API calls (index.js)
├── router/         # Vue Router configuration
├── store/          # State management (user.js)
├── directives/     # Custom directives (permission.js)
├── views/          # Page components
└── App.vue         # Root component
```

### API Client

The frontend uses a centralized axios instance in `src/api/index.js`:
- Base URL: `/api` (proxied through Nginx to Gateway)
- Timeout: 10 seconds
- All API functions are exported from this file

## Database

Each service has its own database. Initialize with:
```bash
mysql -u root -p < init-database.sql
```

Databases:
- `insurance_product` - Product service
- `insurance_customer` - Customer service
- `insurance_application` - Application service
- `insurance_underwriting` - Underwriting service
- `insurance_policy` - Policy service
- `insurance_claims` - Claims service
- `insurance_finance` - Finance service
- `insurance_auth` - Auth service

## Access Points

- Frontend: http://localhost
- Gateway: http://localhost:8888
- Eureka Dashboard: http://localhost:8761
- Spring Boot Admin: http://localhost:8090
- Default credentials: admin / 123456

## Business Flow

### Insurance Application Flow
```
Customer -> Application -> Product Selection -> Submit -> Underwriting -> (Approved) -> Policy
                                                                    -> (Rejected) -> Application Failed
```

### Claims Flow
```
Report Claim -> Application -> Document Review -> Calculation -> Approval -> Payment
```

## Documentation

Additional documentation is available in the `docs/` directory:
- `01-系统设计文档.md` - System Design
- `02-需求规格说明书.md` - Requirements Specification
- `03-功能清单.md` - Feature List
- `04-用户手册.md` - User Manual
- `05-问题修复文档.md` - Bug Fixes Documentation
