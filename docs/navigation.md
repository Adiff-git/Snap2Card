# Snap2Card Navigation Contract & Guide

## Overview
Snap2Card uses **Navigation Compose** with a centralized navigation definition.

- **Single Activity**: `MainActivity` hosts the root `NavGraph`.
- **String Routes Only**: No Parcelable or domain objects cross navigation boundaries.
- **Primitive Arguments**: Screen destinations accept primitive arguments (e.g., `deckId: String`).

---

## Centralized Route Contract (`core/navigation/Screen.kt`)

```kotlin
sealed class Screen(val route: String) {
    // Auth Graph
    data object Splash : Screen("splash")
    data object Login : Screen("login")

    // Main Graph — Bottom Nav Destinations
    data object Home : Screen("home")
    data object DeckList : Screen("deck_list")
    data object Snap2Card : Screen("snap2card")
    data object History : Screen("history")
    data object Settings : Screen("settings")

    // Secondary Destinations
    data object DeckDetail : Screen("deck_detail/{deckId}") {
        fun createRoute(deckId: String) = "deck_detail/$deckId"
    }
    data object CreateDeck : Screen("create_deck")
    data object GeneratedCards : Screen("generated_cards/{jobId}") {
        fun createRoute(jobId: String) = "generated_cards/$jobId"
    }
    data object Study : Screen("study/{deckId}") {
        fun createRoute(deckId: String) = "study/$deckId"
    }
}
```

---

## Bottom Navigation (`core/navigation/BottomNavItem.kt`)

The bottom bar is dynamically toggled for top-level destinations:
- **Home** (`home`)
- **Decks** (`deck_list`)
- **Snap** (`snap2card`)
- **History** (`history`)
- **Settings** (`settings`)

---

## Sub-Graph Structure (`core/navigation/NavGraph.kt`)

1. **`authNavGraph`**: Handles `splash` and `login`.
2. **`mainNavGraph`**: Handles all main and detail routes (`home`, `deck_list`, `deck_detail/{deckId}`, `create_deck`, `snap2card`, `generated_cards/{jobId}`, `study/{deckId}`, `history`, `settings`).

---

## Integration Guidelines for Frontend Developers
- Do NOT alter route paths in `Screen.kt` without agreement from FE1 (Nav Owner).
- Always use `Screen.<Target>.createRoute(...)` when navigating with arguments.
- Pass callback parameters (`onDeckClick: (String) -> Unit`) up to the parent composable in `NavGraph.kt`.
