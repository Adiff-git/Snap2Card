package com.snap2card

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.snap2card.core.navigation.NavGraph
import com.snap2card.design_system.theme.Snap2CardTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single activity — hosts the Compose NavHost.
 * All navigation happens inside the Compose graph; no fragment transactions.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Snap2CardTheme {
                NavGraph()
            }
        }
    }
}
