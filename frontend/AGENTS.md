# FRONTEND KNOWLEDGE

**Location:** `frontend/`

## OVERVIEW

Vue 3 + Element Plus admin dashboard for insurance system.

## STRUCTURE

```
frontend/src/
├── api/index.js       # Axios + JWT token handling
├── router/index.js    # Vue Router
├── views/             # Page components (18 files)
├── directives/       # Custom directives
└── App.vue          # Root component
```

## WHERE TO LOOK

| Task | File | Notes |
|------|------|-------|
| Login | `views/Login.vue` | JWT auth |
| Layout | `views/Layout.vue` | Sidebar nav |
| Product CRUD | `views/ProductList.vue` | Complex form + dialogs |
| API calls | `api/index.js` | Interceptors, baseURL |

## CONVENTIONS

- **API Base**: `/api` (proxied to Gateway port 8888)
- **Auth**: JWT token in `localStorage.getItem('token')`
- **Timeout**: 10 seconds default
- **Components**: Element Plus + Vue 3 Composition API

## ANTI-PATTERNS

- DO NOT hardcode API URLs - use centralized `api/index.js`
- DO NOT use `localhost:8888` directly - use relative `/api`

## COMMANDS

```bash
npm install
npm run dev    # Port 3000
npm run build  # dist/
```
