package com.snap2card.feature.snap2card.domain.model

data class OcrResult(
    val text: String,
    val characterCount: Int,
    val blockCount: Int,
)
