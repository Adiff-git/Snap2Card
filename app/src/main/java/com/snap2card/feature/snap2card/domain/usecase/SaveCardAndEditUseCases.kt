package com.snap2card.feature.snap2card.domain.usecase

import com.snap2card.feature.snap2card.data.remote.CardApiService
import com.snap2card.feature.snap2card.data.remote.dto.CardEditRequest
import javax.inject.Inject

/**
 * Edits an existing backend card through PUT /cards.
 * Wrap this in a CardRepository once card operations are shared by more screens.
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
