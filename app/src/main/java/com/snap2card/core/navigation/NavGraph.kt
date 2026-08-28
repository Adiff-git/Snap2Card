package com.snap2card.core.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.snap2card.design_system.components.navigation.AppBottomNav
import com.snap2card.feature.auth.presentation.LoginScreen
import com.snap2card.feature.auth.presentation.SplashScreen
import com.snap2card.feature.deck.presentation.create.CreateDeckScreen
import com.snap2card.feature.deck.presentation.list.DeckListScreen
import com.snap2card.feature.history.presentation.HistoryScreen
import com.snap2card.feature.home.presentation.HomeScreen
import com.snap2card.feature.settings.presentation.SettingsScreen
import com.snap2card.feature.snap2card.presentation.capture.Snap2CardScreen
import com.snap2card.feature.study.presentation.StudyScreen

/** Bottom nav routes that should show the bottom bar. */
private val bottomNavRoutes = setOf(
    Screen.Home.route,
    Screen.DeckList.route,
    Screen.Snap2Card.route,
    Screen.History.route,
    Screen.Settings.route,
)

/**
 * Root navigation graph.
 * Owner: Dev A — coordinate before modifying this file.
 * Each feature exposes a NavGraphBuilder extension to minimise conflicts.
 */
@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = { if (showBottomBar) AppBottomNav(navController) }
    ) { _ ->
        NavHost(navController = navController, startDestination = Screen.Splash.route) {
            authNavGraph(navController)
            mainNavGraph(navController)
        }
    }
}

// ── Auth Graph ────────────────────────────────────────────────────────────────

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    composable(Screen.Splash.route) {
        SplashScreen(
            onNavigateToLogin = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
            onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
        )
    }
    composable(Screen.Login.route) {
        LoginScreen(onLoginSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } })
    }
}

// ── Main Graph ────────────────────────────────────────────────────────────────

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable(Screen.Home.route) {
        HomeScreen(
            onDeckClick = { deckId -> navController.navigate(Screen.DeckDetail.createRoute(deckId)) },
            onSnapClick = { navController.navigate(Screen.Snap2Card.route) },
        )
    }
    composable(Screen.DeckList.route) {
        DeckListScreen(
            onDeckClick = { deckId -> navController.navigate(Screen.DeckDetail.createRoute(deckId)) },
            onCreateDeck = { navController.navigate(Screen.CreateDeck.route) },
        )
    }
    composable(Screen.CreateDeck.route) {
        CreateDeckScreen(
            onDeckCreated = { deckId -> navController.navigate(Screen.DeckDetail.createRoute(deckId)) { popUpTo(Screen.CreateDeck.route) { inclusive = true } } },
            onNavigateBack = { navController.popBackStack() },
        )
    }
    composable(Screen.DeckDetail.route) { backStackEntry ->
        val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
        // TODO: DeckDetailScreen(deckId = deckId, …)
    }
    composable(Screen.Snap2Card.route) {
        Snap2CardScreen(onCardsGenerated = { jobId -> navController.navigate(Screen.GeneratedCards.createRoute(jobId)) })
    }
    composable(Screen.GeneratedCards.route) { backStackEntry ->
        val jobId = backStackEntry.arguments?.getString("jobId") ?: return@composable
        // TODO: GeneratedCardsScreen(jobId = jobId, …)
    }
    composable(Screen.Study.route) { backStackEntry ->
        val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
        StudyScreen(onFinished = { navController.popBackStack() })
    }
    composable(Screen.History.route) { HistoryScreen() }
    composable(Screen.Settings.route) {
        SettingsScreen(onSignOut = { navController.navigate(Screen.Login.route) { popUpTo(0) } })
    }
}
