package com.snap2card.feature.deck.presentation.create

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.snap2card.core.util.FileUtil
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.components.buttons.SecondaryButton
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.AppBackground
import com.snap2card.design_system.theme.Indigo100
import com.snap2card.design_system.theme.Indigo500
import com.snap2card.design_system.theme.Spacing

@Composable
fun CameraInputScreen(
    onNavigateBack: () -> Unit,
    onUsePhoto: (uri: Uri, mimeType: String, name: String) -> Unit,
) {
    val context = LocalContext.current
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var capturedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var permissionDenied by remember { mutableStateOf(false) }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        capturedPhotoUri = if (success) pendingPhotoUri else null
        pendingPhotoUri = null
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        permissionDenied = !granted
        if (granted) {
            val uri = FileUtil.createTempImageUri(context)
            pendingPhotoUri = uri
            takePictureLauncher.launch(uri)
        }
    }

    fun startCapture() {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            val uri = FileUtil.createTempImageUri(context)
            pendingPhotoUri = uri
            takePictureLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AppTopBar(
                title = "Scan Notes",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
            )
        },
        bottomBar = {
            CameraBottomBar(
                hasPhoto = capturedPhotoUri != null,
                onCapture = { startCapture() },
                onRetake = { startCapture() },
                onUsePhoto = {
                    capturedPhotoUri?.let { uri -> onUsePhoto(uri, "image/jpeg", "Captured photo") }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text(
                text = "Position the page inside the frame",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            if (capturedPhotoUri == null) {
                CameraFrame()
            } else {
                ImagePreview(uri = capturedPhotoUri!!)
            }

            if (permissionDenied) {
                Text(
                    text = "Camera permission is required to scan notes.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun CameraFrame() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.lg)
                .border(2.dp, Indigo100, RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                Surface(modifier = Modifier.size(72.dp), shape = MaterialTheme.shapes.extraLarge, color = Indigo100) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Indigo500, modifier = Modifier.size(34.dp))
                    }
                }
                Text("Camera Preview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "Tap capture to open the camera.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ImagePreview(uri: Uri) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Captured notes preview",
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.sm)
                .clip(MaterialTheme.shapes.large),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun CameraBottomBar(
    hasPhoto: Boolean,
    onCapture: () -> Unit,
    onRetake: () -> Unit,
    onUsePhoto: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        if (hasPhoto) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SecondaryButton(text = "Retake", onClick = onRetake, modifier = Modifier.weight(1f))
                PrimaryButton(text = "Use Photo", onClick = onUsePhoto, modifier = Modifier.weight(1f))
            }
        } else {
            PrimaryButton(
                text = "Capture",
                onClick = onCapture,
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
            )
        }
    }
}
