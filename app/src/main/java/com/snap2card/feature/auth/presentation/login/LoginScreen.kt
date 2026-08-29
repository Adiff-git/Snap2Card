package com.snap2card.feature.auth.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.design_system.components.buttons.GoogleSignInButton
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.components.inputs.AppTextField
import com.snap2card.design_system.theme.Indigo100
import com.snap2card.design_system.theme.Indigo500
import com.snap2card.design_system.theme.Spacing
import com.snap2card.design_system.theme.White
import com.snap2card.feature.auth.presentation.LoginUiState
import com.snap2card.feature.auth.presentation.LoginViewModel

/**
 * Login screen — matches the design with illustration, email/password fields,
 * forgot-password link, "Log In" CTA, OR divider, and "Sign in with Google" button.
 *
 * Email/password fields are visual-only for now; only the Google button is wired.
 *
 * Owner: FE1
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) onLoginSuccess()
    }

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg)
            .padding(top = Spacing.xxxl, bottom = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ── Illustration area ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Indigo100),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = Indigo500,
            )
        }

        Spacer(Modifier.height(Spacing.xl))

        // ── Headline ───────────────────────────────────────────────────
        Text(
            text = "Welcome back to\nSnap2Card",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = "Log in to continue your learning journey.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(Spacing.xl))

        // ── Email field ────────────────────────────────────────────────
        AppTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            placeholder = "Enter your email",
            leadingIcon = {
                Icon(Icons.Outlined.Email, contentDescription = null)
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(Spacing.md))

        // ── Password field ─────────────────────────────────────────────
        AppTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            placeholder = "Enter your password",
            leadingIcon = {
                Icon(Icons.Outlined.Lock, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Outlined.VisibilityOff
                        else Icons.Outlined.Visibility,
                        contentDescription = "Toggle password visibility",
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
        )

        // Forgot password link
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { /* TODO: Forgot password flow */ }) {
                Text(
                    "Forgot password?",
                    style = MaterialTheme.typography.labelLarge,
                    color = Indigo500,
                )
            }
        }

        Spacer(Modifier.height(Spacing.md))

        // ── Log In button ──────────────────────────────────────────────
        PrimaryButton(
            text = "Log In",
            onClick = { /* Email/password is visual-only for now */ },
        )

        Spacer(Modifier.height(Spacing.lg))

        // ── OR divider ─────────────────────────────────────────────────
        OrDivider()

        Spacer(Modifier.height(Spacing.lg))

        // ── Google Sign-In ─────────────────────────────────────────────
        GoogleSignInButton(
            onClick = {
                // TODO: Launch Google Credential Manager flow, then call viewModel.signInWithGoogle(idToken)
            },
            enabled = uiState !is LoginUiState.Loading,
        )

        // Error message
        if (uiState is LoginUiState.Error) {
            Spacer(Modifier.height(Spacing.md))
            Text(
                text = (uiState as LoginUiState.Error).message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.weight(1f))

        // ── Sign-up prompt ─────────────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "Don't have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            TextButton(onClick = { /* TODO: Navigate to sign-up */ }) {
                Text(
                    "Sign up",
                    style = MaterialTheme.typography.labelLarge,
                    color = Indigo500,
                )
            }
        }
    }
}

/**
 * "OR" horizontal divider — a thin line with "OR" centred on top.
 */
@Composable
private fun OrDivider(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
        Text(
            text = "  OR  ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
    }
}
