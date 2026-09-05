package com.snap2card.feature.snap2card.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ImageDto(
    val image: String,
    val mimeType: String,
)

@Serializable
data class CardCreateRequest(
    val name: String,
    val type: String,
    val text: String? = null,
    val image: ImageDto? = null,
    val frontSide: String? = null,
    val backSide: String? = null,
)

@Serializable
data class CardCreateData(val id: String)

@Serializable
data class CardDocumentGenerationData(
    val numOfCard: Int,
    val cards: List<CardDocumentGeneratedCardDto>,
)

@Serializable
data class CardDocumentGeneratedCardDto(
    val frontSide: String,
    val backSide: String,
)

@Serializable
data class CardDto(val id: String, val frontSide: String, val backSide: String)

@Serializable
data class CardEditRequest(
    val id: String,
    val frontSide: String? = null,
    val backSide: String? = null,
    val categories: List<String>? = null,
)
