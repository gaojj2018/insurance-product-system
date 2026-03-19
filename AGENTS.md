# PROJECT KNOWLEDGE BASE

**Generated:** 2026-03-18
**Project:** Insurance Product Management System
**Type:** Spring Cloud Microservices + Vue.js Frontend

## OVERVIEW

Multi-service insurance product management system with 13 backend services (Spring Boot 3.2.0, Java 17) and Vue.js frontend.

## STRUCTURE

```
insurance-product-system/
├── eureka-server/       # Service Registry (port 8761)
├── config-service/      # Config Server (port 8089)
├── gateway/             # API Gateway (port 8888)
├── admin-server/        # Spring Boot Admin (port 8090)
├── auth-service/       # Authentication (port 8092)
├── product-service/    # Product management (port 8080)
├── customer-service/   # Customer management (port 8082)
├── application-service/ # Insurance application (port 8083)
├── underwriting-service/# Underwriting (port 8084)
├── policy-service/     # Policy management (port 8085)
├── claims-service/     # Claims processing (port 8086)
├── finance-service/    # Finance operations (port 8087)
├── message-service/    # Notifications (port 8088)
├── report-service/     # Reporting
├── frontend/           # Vue 3 + Element Plus
├── bat/                # Windows batch scripts
├── py/                 # Python E2E tests
└── docs/               # Documentation
```

## WHERE TO LOOK

| Task | Location | Notes |
|------|----------|-------|
| API endpoints | `*/controller/*.java` | RESTful controllers |
| Business logic | `*/service/*.java` | Service layer |
| Database access | `*/repository/*.Mapper.java` | MyBatis Plus |
| Entities | `*/entity/*.java` | Domain models |
| Frontend views | `frontend/src/views/*.vue` | Page components |
| API calls | `frontend/src/api/index.js` | Axios config |

## REPORT APIS

| Endpoint | Service | Description |
|----------|---------|-------------|
| GET /api/report/dashboard | report-service | 仪表盘统计数据 |
| GET /api/report/policy | report-service | 保单统计 |
| GET /api/report/claim | report-service | 理赔统计 |
| GET /api/report/product | report-service | 产品分布统计 |
| GET /api/premium/statistics | finance-service | 保费统计数据 |
| GET /api/claims/statistics | claims-service | 理赔统计数据 |

## SCHEDULED TASKS

| Task | Service | Cron | Description |
|------|---------|------|-------------|
| 佣金计算 | finance-service | 0 0 1 * * ? | 计算已缴费保单的佣金 |
| 续期处理 | policy-service | 0 0 2 * * ? | 检查保单到期并发送提醒 |
| 理赔结算 | claims-service | 0 0 3 * * ? | 结算已审核通过的理赔 |
| 小额理赔审核 | claims-service | 0 0 4 * * ? | 自动审核万元以下理赔 |

## CONVENTIONS

- **API Response**: `{ code: 200, message: "...", data: {...} }`
- **URL Pattern**: `POST /api/{resource}/page` for pagination
- **Entity**: Use `@TableLogic` for soft delete
- **Ports**: See structure above (Eureka self-register)
- **Frontend**: Path alias `@` → `src/`

## ANTI-PATTERNS

- DO NOT use `localhost` in config - use env vars `${DB_HOST:localhost}`
- DO NOT hardcode IPs/ports - use Eureka service discovery
- AVOID duplicate underwriting module (use `underwriting-service/`)
- AVOID loose Java files in `docs/` - use proper Maven modules

## COMMANDS

```bash
# Build all (from root)
mvn clean package -DskipTests

# Start order (after Eureka)
# 1. config-service → 2. gateway → 3. auth-service → 4. business services

# Frontend
cd frontend && npm run dev    # Port 3000
npm run build                  # Output: dist/
```

## NOTES

- Shared code currently scattered in `docs/common/` - consider creating `common/` Maven module
- Duplicate `underwriting/` vs `underwriting-service/` - use latter
- Frontend proxies `/api` → `http://localhost:8888` (Gateway)
