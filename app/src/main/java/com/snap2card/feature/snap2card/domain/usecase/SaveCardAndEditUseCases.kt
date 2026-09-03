package com.snap2card.feature.snap2card.domain.usecase

import com.snap2card.feature.snap2card.data.remote.CardApiService
import com.snap2card.feature.snap2card.data.remote.dto.CardEditRequest
import javax.inject.Inject

/**
 * Replaces the old SaveGeneratedCardsUseCase.
 *
 * IMPORTANT: the card is already persisted server-side as soon as
 * submitImage()/submitDocument() succeeds (see OcrRepositoryImpl).
 * There is no separate "save" step that creates the card — "saving" the
 * review screen now means editing the already-created card via PUT /cards,
 * only if the user changed the front/back text or wants to assign categories.
 *
 * If the user didn't touch anything, you can skip calling this entirely.
 *
 * NOTE: this depends directly on CardApiService rather than a repository
 * interface for now — wrap it in a proper CardRepository once that's built
 * out for the Deck feature, since PUT /cards will also be used there.
 */
class SaveCardEditsUseCase @Inject constructor(
    private val apiService: CardApiService,
) {
    suspend operator fun invoke(
        cardId: String,
        frontSide: String,
        backSide: String,
        categoryIds: List<String>? = null,
    ): Result<Unit> = runCatching {
        apiService.editCard(
            CardEditRequest(
                id = cardId,
                frontSide = frontSide,
                backSide = backSide,
                categories = categoryIds,
            )
        )
        Unit
    }
}