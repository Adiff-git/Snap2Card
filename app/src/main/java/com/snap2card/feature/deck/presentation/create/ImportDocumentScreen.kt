package com.snap2card.feature.deck.presentation.create

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.UploadFile
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.snap2card.core.util.FileUtil
import com.snap2card.design_system.components.buttons.PrimaryButton
import com.snap2card.design_system.components.buttons.SecondaryButton
import com.snap2card.design_system.components.navigation.AppTopBar
import com.snap2card.design_system.theme.AppBackground
import com.snap2card.design_system.theme.Indigo100
import com.snap2card.design_system.theme.Indigo500
import com.snap2card.design_system.theme.Spacing

private data class SelectedDocument(
    val uri: Uri,
    val name: String,
    val mimeType: String,
    val sizeBytes: Long?,
)

@Composable
fun ImportDocumentScreen(
    onNavigateBack: () -> Unit,
    onGenerateCards: (uri: Uri, mimeType: String, name: String) -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var selectedDocument by remember { mutableStateOf<SelectedDocument?>(null) }
    val documentPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            selectedDocument = SelectedDocument(
                uri = uri,
                name = FileUtil.getDisplayName(context, uri),
                mimeType = FileUtil.getMimeType(context, uri),
                sizeBytes = FileUtil.getFileSize(context, uri),
            )
        }
    }

    fun chooseFile() {
        documentPicker.launch(arrayOf("application/pdf", "image/*"))
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            AppTopBar(
                title = "Import Document",
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = onNavigateBack,
            )
        },
        bottomBar = {
            if (selectedDocument != null) {
                Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                    PrimaryButton(
                        text = "Generate Cards",
                        onClick = {
                            selectedDocument?.let { onGenerateCards(it.uri, it.mimeType, it.name) }
                        },
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(AppBackground)
                .padding(Spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            if (selectedDocument == null) {
                ImportEmptyState(onChooseFile = { chooseFile() })
            } else {
                SelectedDocumentState(
                    document = selectedDocument!!,
                    onReplace = { chooseFile() },
                    onRemove = { selectedDocument = null },
                )
            }
        }
    }
}

@Composable
private fun ImportEmptyState(onChooseFile: () -> Unit) {
    Spacer(Modifier.size(Spacing.xxl))
    Surface(modifier = Modifier.size(88.dp), shape = MaterialTheme.shapes.extraLarge, color = Indigo100) {
        Box(contentAlignment = Alignment.Center) {
            Icon(Icons.Default.UploadFile, contentDescription = null, tint = Indigo500, modifier = Modifier.size(42.dp))
        }
    }
    Text(
        text = "Choose a file to generate flashcards",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
    )
    Text(
        text = "Supported types: PDF files and images.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
    PrimaryButton(text = "Choose File", onClick = onChooseFile)
    Spacer(Modifier.size(Spacing.xxl))
}

@Composable
private fun SelectedDocumentState(
    document: SelectedDocument,
    onReplace: () -> Unit,
    onRemove: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, Indigo100, MaterialTheme.shapes.large)
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(52.dp), shape = MaterialTheme.shapes.large, color = Indigo100) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = null, tint = Indigo500)
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = document.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = buildFileInfo(document),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                SecondaryButton(text = "Replace", onClick = onReplace, modifier = Modifier.weight(1f))
                SecondaryButton(text = "Remove", onClick = onRemove, modifier = Modifier.weight(1f))
            }
        }
    }

    Text(
        text = "Review the selected file, then generate cards when ready.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth(),
    )
}

private fun buildFileInfo(document: SelectedDocument): String {
    val size = document.sizeBytes?.let { formatFileSize(it) }
    return listOfNotNull(document.mimeType, size).joinToString(" • ")
}

private fun formatFileSize(bytes: Long): String = when {
    bytes >= 1_000_000 -> "%.1f MB".format(bytes / 1_000_000.0)
    bytes >= 1_000 -> "%.1f KB".format(bytes / 1_000.0)
    else -> "$bytes B"
}
