package com.snap2card.feature.snap2card.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GeneratedCardDto(
    @SerialName("front") val front: String,
    @SerialName("back") val back: String,
)

@Serializable
data class OcrUploadResponse(
    @SerialName("job_id") val jobId: String,
    @SerialName("cards") val cards: List<GeneratedCardDto>,
    @SerialName("message") val message: String? = null,
)
