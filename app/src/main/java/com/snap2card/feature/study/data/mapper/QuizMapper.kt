package com.snap2card.feature.study.data.mapper

import com.snap2card.core.util.DateUtil
import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.study.domain.model.ReviewQuiz

fun ReviewQuiz.toCard(deckId: String): Card = Card(
    id = quizId,              // repurposed to carry the server quizId through the session
    deckId = deckId,
    front = front,
    back = back,
    createdAt = DateUtil.now(), // API doesn't return one; these cards aren't persisted, just held in-memory for the session
)