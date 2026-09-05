package com.snap2card.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.snap2card.design_system.components.navigation.AppBottomNav
import com.snap2card.feature.account.presentation.AccountScreen
import com.snap2card.feature.auth.presentation.login.LoginScreen
import com.snap2card.feature.auth.presentation.splash.SplashScreen
import com.snap2card.feature.deck.presentation.create.CreateDeckScreen
import com.snap2card.feature.deck.presentation.edit.EditDeckScreen
import com.snap2card.feature.deck.presentation.list.DeckListScreen
import com.snap2card.feature.history.presentation.HistoryScreen
import com.snap2card.feature.home.presentation.HomeScreen
import com.snap2card.feature.settings.presentation.SettingsScreen
import com.snap2card.feature.snap2card.presentation.capture.CameraInputScreen
import com.snap2card.feature.snap2card.presentation.capture.ImportDocumentScreen
import com.snap2card.feature.snap2card.presentation.capture.ManualCardEditorScreen
import com.snap2card.feature.snap2card.presentation.review.CardGenerationInputScreen
import com.snap2card.feature.snap2card.presentation.review.GeneratedCardsScreen
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            enterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) },
            exitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) },
            popEnterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) },
            popExitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) },
        ) {
            authNavGraph(navController)
            mainNavGraph(navController)
            accountNavGraph(navController)
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
            onSnapClick = {
                navController.navigate(Screen.Snap2Card.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        )
    }
    composable(Screen.DeckList.route) {
        DeckListScreen(
            onDeckClick = { deckId -> navController.navigate(Screen.DeckDetail.createRoute(deckId)) },
            onCreateDeck = {
                navController.navigate(Screen.Snap2Card.route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        )
    }
    composable(Screen.CreateDeckCamera.route) { backStackEntry ->
        val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
        CameraInputScreen(
            onNavigateBack = { navController.popBackStack() },
            onUsePhoto = { uri, mimeType, name ->
                navController.navigate(Screen.CardGenerationInput.createRoute(deckId, "camera", uri.toString(), mimeType, name))
            },
        )
    }
    composable(Screen.CreateDeckDocument.route) { backStackEntry ->
        val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
        ImportDocumentScreen(
            onNavigateBack = { navController.popBackStack() },
            onGenerateCards = { uri, mimeType, name ->
                navController.navigate(Screen.CardGenerationInput.createRoute(deckId, "document", uri.toString(), mimeType, name))
            },
        )
    }
    composable(Screen.CreateDeckManual.route) { backStackEntry ->
        val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
        ManualCardEditorScreen(
            deckId = deckId,
            onNavigateBack = { navController.popBackStack() },
            onCardsSaved = { navController.popBackStack() },
        )
    }
    composable(Screen.DeckDetail.route) { backStackEntry ->
        val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
        EditDeckScreen(
            deckId = deckId,
            onNavigateBack = { navController.popBackStack() },
            onDeckSaved = { navController.navigate(Screen.DeckList.route) { popUpTo(Screen.DeckList.route) { inclusive = true } } },
            onStudyClick = { id -> navController.navigate(Screen.Study.createRoute(id)) },
        )
    }
    composable(Screen.EditDeck.route) { backStackEntry ->
        val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
        EditDeckScreen(
            deckId = deckId,
            onNavigateBack = { navController.popBackStack() },
            onDeckSaved = { navController.popBackStack() },
            onStudyClick = { id -> navController.navigate(Screen.Study.createRoute(id)) },
        )
    }
    composable(Screen.Snap2Card.route) {
        CreateDeckScreen(
            onDeckCreated = { deckId -> navController.navigate(Screen.DeckDetail.createRoute(deckId)) },
            onScanWithCamera = { deckId -> navController.navigate(Screen.CreateDeckCamera.createRoute(deckId)) },
            onImportDocument = { deckId -> navController.navigate(Screen.CreateDeckDocument.createRoute(deckId)) },
            onAddCardsManually = { deckId -> navController.navigate(Screen.CreateDeckManual.createRoute(deckId)) },
            onNavigateBack = { navController.popBackStack() },
        )
    }
    composable(Screen.CardGenerationInput.route) { backStackEntry ->
        val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
        CardGenerationInputScreen(
            deckId = deckId,
            onNavigateBack = { navController.popBackStack() },
            onCardsGenerated = { jobId ->
                navController.navigate(Screen.GeneratedCards.createRoute(deckId, jobId)) {
                    popUpTo(Screen.CardGenerationInput.route) { inclusive = true }
                }
            },
        )
    }
    composable(Screen.GeneratedCards.route) {
        GeneratedCardsScreen(
            onNavigateBack = { navController.popBackStack() },
            onDeckSaved = { deckId ->
                navController.navigate(Screen.DeckDetail.createRoute(deckId)) {
                    popUpTo(Screen.GeneratedCards.route) { inclusive = true }
                }
            },
        )
    }
    composable(Screen.Study.route) { backStackEntry ->
        val deckId = backStackEntry.arguments?.getString("deckId") ?: return@composable
        StudyScreen(onFinished = { navController.popBackStack() })
    }
    composable(Screen.History.route) { HistoryScreen() }
    composable(Screen.Settings.route) {
        SettingsScreen(
            onSignOut = { navController.navigate(Screen.Login.route) { popUpTo(0) } },
            onAccountClick = { navController.navigate(Screen.Account.route) },
        )
    }
}

// ── Account Graph ────────────────────────────────────────────────────────────

fun NavGraphBuilder.accountNavGraph(navController: NavHostController) {
    composable(Screen.Account.route) {
        AccountScreen(onNavigateBack = { navController.popBackStack() })
    }
}
