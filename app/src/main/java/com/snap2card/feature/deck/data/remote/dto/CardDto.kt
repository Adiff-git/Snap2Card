package com.snap2card.feature.deck.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardDto(
    @SerialName("id") val id: String,
    @SerialName("deck_id") val deckId: String,
    @SerialName("front") val front: String,
    @SerialName("back") val back: String,
    @SerialName("created_at") val createdAt: Long,
)
