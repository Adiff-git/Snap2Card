package com.snap2card.design_system.theme

import androidx.compose.ui.graphics.Color

/**
 * ─────────────────────────────────────────────
 *  SINGLE SOURCE OF TRUTH FOR ALL COLOR TOKENS
 *  Do NOT use raw Color(0xFF…) anywhere outside this file.
 * ─────────────────────────────────────────────
 */

// Primary palette — soft indigo
val Indigo500 = Color(0xFF4F63F0)
val Indigo400 = Color(0xFF6F7FF2)
val Indigo300 = Color(0xFF8F9CF5)
val Indigo100 = Color(0xFFE4E7FD)
val Indigo50  = Color(0xFFF1F2FE)

// Neutrals
val White     = Color(0xFFFFFFFF)
val Gray50    = Color(0xFFF9FAFB)
val Gray100   = Color(0xFFF3F4F6)
val Gray200   = Color(0xFFE5E7EB)
val Gray400   = Color(0xFF9CA3AF)
val Gray600   = Color(0xFF4B5563)
val Gray800   = Color(0xFF1F2937)
val Gray900   = Color(0xFF111827)
val Black     = Color(0xFF000000)

// Semantic
val Success   = Color(0xFF22C55E)
val Warning   = Color(0xFFF59E0B)
val Error     = Color(0xFFEF4444)
val Info      = Color(0xFF3B82F6)

// Surface overlay for dark mode
val SurfaceDark = Color(0xFF1A1C2A)
val SurfaceVariantDark = Color(0xFF252840)

// ── Gradients ──────────────────────────────────────────────────────────
// Splash screen background gradient (soft indigo → white)
val SplashGradientStart = Color(0xFFD6DBFC)
val SplashGradientEnd = Color(0xFFF1F2FE)

// Capture card gradient (primary CTA on Home)
val CaptureGradientStart = Indigo500
val CaptureGradientEnd = Indigo300

// ── Category tag / chip colours ────────────────────────────────────────
val TagGreen = Color(0xFF22C55E)
val TagGreenBg = Color(0xFFDCFCE7)
val TagOrange = Color(0xFFF59E0B)
val TagOrangeBg = Color(0xFFFEF3C7)
val TagBlue = Color(0xFF3B82F6)
val TagBlueBg = Color(0xFFDBEAFE)
val TagPurple = Color(0xFF8B5CF6)
val TagPurpleBg = Color(0xFFEDE9FE)
val TagRed = Color(0xFFEF4444)
val TagRedBg = Color(0xFFFEE2E2)
