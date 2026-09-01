package com.snap2card

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.snap2card.core.navigation.NavGraph
import com.snap2card.design_system.theme.Snap2CardTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single activity — hosts the Compose NavHost.
 * All navigation happens inside the Compose graph; no fragment transactions.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val themeViewModel: AppThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by themeViewModel.darkTheme.collectAsStateWithLifecycle()
            Snap2CardTheme(darkTheme = isDarkTheme) {
                NavGraph()
            }
        }
    }
}
