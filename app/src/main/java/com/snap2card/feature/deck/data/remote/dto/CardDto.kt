package com.snap2card.feature.deck.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardListResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: CardListData,
)

@Serializable
data class CardListData(
    @SerialName("numOfCard") val numOfCard: Int,
    @SerialName("cards") val cards: List<CardListItemDto>,
)

@Serializable
data class CardListItemDto(
    @SerialName("id") val id: String,
    @SerialName("frontSide") val frontSide: String,
)

@Serializable
data class CardRetrieveRequest(
    @SerialName("ids") val ids: List<String>,
)

@Serializable
data class CardRetrieveResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: List<CardDetailDto>,
)

@Serializable
data class CardDetailDto(
    @SerialName("id") val id: String,
    @SerialName("frontSide") val frontSide: String,
    @SerialName("backSide") val backSide: String,
)

@Serializable
data class CardCreateRequest(
    @SerialName("name") val name: String,
    @SerialName("type") val type: String = "manual",
    @SerialName("frontSide") val frontSide: String,
    @SerialName("backSide") val backSide: String,
)

@Serializable
data class CardCreateResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: CardCreateData? = null,
)

@Serializable
data class CardCreateData(
    @SerialName("id") val id: String? = null,
    @SerialName("numOfCard") val numOfCard: Int? = null,
    @SerialName("cards") val cards: List<CardListItemDto>? = null,
)
