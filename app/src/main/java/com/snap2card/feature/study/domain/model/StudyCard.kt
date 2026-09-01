package com.snap2card.feature.study.domain.model

data class StudyCard(
    val cardId: String,
    val deckId: String,
    val front: String,
    val back: String,
)