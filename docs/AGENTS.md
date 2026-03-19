# SHARED UTILITIES

**Location:** `docs/`

## OVERVIEW

Loose shared utilities - NOT a proper Maven module. Consider consolidating into `common/` module.

## CONTENTS

```
docs/
├── common/           # Shared Java utilities (loose files)
│   ├── Result.java        # API response wrapper
│   ├── BusinessException.java
│   └── WebMvcConfig.java
├── UserContext.java  # User context holder
└── CLAUDE.md        # Previous AI assistant notes
```

## NOTES

- These files are NOT in Maven reactor - must be manually included
- RECOMMENDATION: Create proper `common/` Maven module
