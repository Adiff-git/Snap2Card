package com.snap2card.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom navigation items with their screen routes and icons.
 */
enum class BottomNavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
) {
    HOME(Screen.Home, "Home", Icons.Filled.Home),
    DECKS(Screen.DeckList, "Decks", Icons.Filled.GridView),
    SNAP(Screen.Snap2Card, "Snap", Icons.Filled.PhotoCamera),
    HISTORY(Screen.History, "History", Icons.Filled.History),
    SETTINGS(Screen.Settings, "Settings", Icons.Filled.Settings),
}
