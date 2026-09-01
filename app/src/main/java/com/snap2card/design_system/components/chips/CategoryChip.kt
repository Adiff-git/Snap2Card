package com.snap2card.design_system.components.chips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.snap2card.design_system.theme.*

/**
 * Small coloured category tag chip shown on deck cards.
 * Matches the design's rounded pill with subtle background tint.
 */
@Composable
fun CategoryChip(
    label: String,
    modifier: Modifier = Modifier,
    textColor: Color = TagGreen,
    backgroundColor: Color = TagGreenBg,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs)
    )
}

/**
 * Resolves a tag name to its matching colour pair.
 * Keeps colour logic centralised rather than scattered through feature screens.
 */
fun tagColors(tag: String): Pair<Color, Color> = when (tag.lowercase()) {
    "science", "biology", "medical" -> TagGreen to TagGreenBg
    "language"                      -> TagOrange to TagOrangeBg
    "history"                       -> TagBlue to TagBlueBg
    "math", "programming"           -> TagPurple to TagPurpleBg
    else                            -> TagBlue to TagBlueBg
}
