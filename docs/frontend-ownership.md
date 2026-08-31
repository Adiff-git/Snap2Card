# Frontend Ownership & Responsibilities

## Team Breakdown

| Role | Scope / Features | Owned Packages & Files |
|------|------------------|------------------------|
| **FE1** (Lead / Foundation) | Design System, Auth, Splash, Home, App Navigation | `com.snap2card.design_system/**`<br>`feature/auth/**`<br>`feature/home/**`<br>`core/navigation/**`<br>`MainActivity.kt` |
| **FE2** | Deck Management & Card Review/Edit | `feature/deck/**`<br>Deck detail, deck creation, review & edit screens |
| **FE3** | Snap/OCR Integration, Study Mode, History, Settings | `feature/snap2card/**`<br>`feature/study/**`<br>`feature/history/**`<br>`feature/settings/**` |

---

## Guidelines for Parallel Work
1. **Design System**: All developers MUST use components and design tokens from `com.snap2card.design_system`. Do NOT create custom color constants or duplicate button/card implementations.
2. **Navigation**: Coordinate with FE1 before modifying `core/navigation/Screen.kt` or `core/navigation/NavGraph.kt`.
3. **Dependency Injection**: Add new repository bindings to `di/RepositoryModule.kt` using `@Binds`.
4. **Git Etiquette**: Avoid modifying files owned by another developer unless coordinate in advance.
