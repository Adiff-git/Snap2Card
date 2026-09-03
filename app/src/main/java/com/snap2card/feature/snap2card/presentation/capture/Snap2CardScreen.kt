package com.snap2card.feature.snap2card.presentation.capture

import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.snap2card.core.util.FileUtil
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.components.buttons.SecondaryButton
import com.snap2card.design_system.components.feedback.LoadingIndicator
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.Spacing

@Composable
fun Snap2CardScreen(
    onCardsGenerated: (jobId: String) -> Unit,
    viewModel: Snap2CardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Holds the Uri we told the camera to write the photo into
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    // --- Gallery / file picker ---
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            viewModel.onInputCancelled()
        } else {
            viewModel.onImageSelected(uri)
        }
    }

    // --- Camera capture ---
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            pendingCameraUri?.let { viewModel.onImageSelected(it) } ?: viewModel.onInputUnavailable()
        } else {
            viewModel.onInputCancelled()
        }
    }

    // --- Camera permission ---
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = FileUtil.createTempImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            viewModel.onCameraPermissionDenied()
        }
    }

    fun launchCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val uri = FileUtil.createTempImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is Snap2CardUiState.Success) {
            onCardsGenerated(state.jobId)
        }
    }

    Scaffold(topBar = { AppTopBar(title = "Snap2Card") }) { padding ->
        Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            when (uiState) {
                is Snap2CardUiState.Idle -> IdleContent(
                    onCameraClick = { launchCamera() },
                    onUploadClick = { galleryLauncher.launch("image/*") },
                )
                is Snap2CardUiState.ExtractingText -> LoadingIndicator(message = "Scanning document...")
                is Snap2CardUiState.GeneratingCards -> LoadingIndicator(message = "Generating cards...")
                is Snap2CardUiState.Success -> LoadingIndicator(message = "Done! Opening results…")
                is Snap2CardUiState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text((uiState as Snap2CardUiState.Error).message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(Spacing.md))
                    PrimaryButton("Try Again", onClick = viewModel::reset)
                }
            }
        }
    }
}

@Composable
private fun IdleContent(onCameraClick: () -> Unit, onUploadClick: () -> Unit) {
    Column(Modifier.padding(Spacing.lg).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Text("Create cards from an image or document", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(Spacing.md))
        PrimaryButton("📷  Scan from Camera", onClick = onCameraClick)
        SecondaryButton("📄  Select Image", onClick = onUploadClick)
    }
}
