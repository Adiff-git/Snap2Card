package com.snap2card.feature.snap2card.domain.model

/**
 * Domain model for a generated flashcard.
 * `id` is now required — the review/edit/save steps (PUT /cards) need it,
 * whereas the old single-call flow may not have carried an id at this stage.
 */
data class GeneratedCard(
    val id: String,
    val front: String,
    val back: String,
)