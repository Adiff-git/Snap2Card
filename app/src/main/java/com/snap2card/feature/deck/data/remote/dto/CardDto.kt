package com.snap2card.feature.deck.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardDto(val id: String, val frontSide: String, val backSide: String)

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
    val ids: List<String>? = null
)

@Serializable
data class CardRetrieveResponse(
    val status: String, val data: List<CardDto> = emptyList()
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

@Serializable
data class CategoryCreateRequest(val name: String)
@Serializable
data class CategoryCreateData(val categoryId: String)
@Serializable
data class CategoryCreateResponse(val status: String, val data: CategoryCreateData)
@Serializable
data class CardCategorizeRequest(val cardId: String, val categoryIds: List<String>)
@Serializable
data class CardCategorizeResponse(val status: String)

@Serializable
data class CardEditResponse(val status: String)

@Serializable
data class CardEditRequest(
    val id: String,
    val frontSide: String,
    val backSide: String,
    // val categoryIds: List<String>? = null  — only if card-edit.md confirms this field exists
)