# Snap2Card Application Architecture

## Overview
Snap2Card is built using a modern Android architecture based on **Clean Architecture**, **MVVM (Model-View-ViewModel)**, and a **Package-by-Feature** modular code layout.

---

## Architectural Layers

### 1. Presentation Layer (`feature/<feature_name>/presentation/`)
- Built entirely with **Jetpack Compose**.
- Driven by a single **`StateFlow<UiState>`** per ViewModel.
- Handles user interactions, UI state rendering, and navigation triggers.
- Strictly observes design system tokens and reusable UI components located in `com.snap2card.design_system`.

### 2. Domain Layer (`feature/<feature_name>/domain/`)
- Contains domain models, repository interfaces, and business logic use cases.
- Completely framework-agnostic (no Android/Jetpack Compose dependencies).
- Use cases encapsulate single operations (e.g., `CheckSessionUseCase`, `GetDashboardUseCase`).

### 3. Data Layer (`feature/<feature_name>/data/`)
- Implements repository contracts defined in the Domain layer.
- Coordinates data sources:
  - Local Database: **Room** (`core/database`)
  - User Preferences / Token Store: **Jetpack DataStore** (`core/datastore`)
  - Remote API Services: **Retrofit + OkHttp** (`core/network` and `feature/<feature_name>/data/remote`)

---

## Dependency Injection (Hilt)
- **`Snap2CardApp`**: Annotated with `@HiltAndroidApp`.
- **`MainActivity`**: Annotated with `@AndroidEntryPoint`.
- **Core Modules (`di/`)**:
  - `DatabaseModule`: Room DB and DAOs.
  - `DataStoreModule`: Preferences DataStore.
  - `NetworkModule`: OkHttp client with auth interceptors, Retrofit instances, Json serialization.
  - `ApiServiceModule`: Retrofit service bindings.
  - `RepositoryModule`: Repository interface to implementation bindings (`@Binds`).

---

## Design System (`com.snap2card.design_system`)
- **Theme (`theme/`)**: Colors (`Indigo500` palette), Typography (`Inter` typeface), Shapes, Spacing (`LocalSpacing` provider).
- **Components (`components/`)**: Reusable buttons (`PrimaryButton`, `SecondaryButton`, `GoogleSignInButton`), cards (`DeckCard`, `FlashCard`), feedback indicators (`AnimatedLoadingDots`, `LoadingIndicator`), chips (`CategoryChip`), and navigation bars (`AppTopBar`, `AppBottomNav`).
