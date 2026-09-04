package com.wanderlab.snap2card.controllers.serverconnection.definitions

data class CreateCardRequest(
    val frontSide: String,
    val backSide: String
)

data class CardInfo(
    val id: String? = null,
    val frontSide: String? = null,
    val backSide: String? = null
)

data class CreateCardsResponse(
    val numOfCard: Int,
    val cards: List<CardInfo>
)

data class EditCardRequest(
    val id: String,
    val frontSide: String? = null,
    val backSide: String? = null,
    val categories: List<String>? = null
)

data class CardListResponse(
    val numOfCard: Int,
    val cards: List<CardInfo>
)