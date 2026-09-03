package com.snap2card.core.navigation

import android.net.Uri

/**
 * All navigation routes in one place.
 * Only string routes are used — no Parcelable/Serializable objects cross nav boundaries.
 * Arguments are passed by primitive ID only (e.g. deckId: String).
 *
 * Owner: Dev A
 */
sealed class Screen(val route: String) {
    // Auth graph
    data object Splash : Screen("splash")
    data object Login : Screen("login")

    // Main graph — bottom nav destinations
    data object Home : Screen("home")
    data object DeckList : Screen("deck_list")
    data object Snap2Card : Screen("snap2card")
    data object History : Screen("history")
    data object Settings : Screen("settings")
    data object Account : Screen("account")

    // Secondary destinations
    data object DeckDetail : Screen("deck_detail/{deckId}") {
        fun createRoute(deckId: String) = "deck_detail/$deckId"
    }
    data object EditDeck : Screen("edit_deck/{deckId}") {
        fun createRoute(deckId: String) = "edit_deck/$deckId"
    }
    data object CreateDeckCamera : Screen("create_deck/camera")
    data object CreateDeckDocument : Screen("create_deck/document")
    data object CreateDeckManual : Screen("create_deck/manual")
    data object CardGenerationInput : Screen(
        "card_generation_input/{sourceType}?uri={uri}&mimeType={mimeType}&name={name}"
    ) {
        fun createRoute(sourceType: String, uri: String, mimeType: String, name: String? = null): String =
            "card_generation_input/${Uri.encode(sourceType)}" +
                "?uri=${Uri.encode(uri)}" +
                "&mimeType=${Uri.encode(mimeType)}" +
                "&name=${Uri.encode(name.orEmpty())}"
    }
    data object GeneratedCards : Screen("generated_cards/{jobId}") {
        fun createRoute(jobId: String) = "generated_cards/${Uri.encode(jobId)}"
    }
    data object Study : Screen("study/{deckId}") {
        fun createRoute(deckId: String) = "study/$deckId"
    }
}
