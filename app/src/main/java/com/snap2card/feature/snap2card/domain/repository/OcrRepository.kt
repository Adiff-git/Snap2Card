package com.snap2card.feature.snap2card.domain.repository

import android.net.Uri
import com.snap2card.feature.snap2card.domain.model.GeneratedCard

/**
 * Domain contract for turning a captured image/document into a flashcard.
 *
 * IMPORTANT: this is a two-step contract, matching the real API
 * (card-create.md + card-retrieve.md) — there is no single synchronous
 * "give me the generated card" call. POST /cards returns only an id;
 * the generated frontSide/backSide must be fetched separately.
 */
interface OcrRepository {

    /**
     * Submits an image for card generation.
     * Maps to POST /cards with type = "image".
     * Returns the created card's id on success.
     */
    suspend fun submitImage(uri: Uri, mimeType: String, name: String): Result<String>

    /**
     * Submits raw extracted/pasted text for card generation.
     * Maps to POST /cards with type = "document".
     */
    suspend fun submitDocument(text: String, name: String): Result<String>

    /**
     * Fetches a single generated card by id.
     * Maps to GET /cards?ids=<id>.
     */
    suspend fun getGeneratedCard(cardId: String): Result<GeneratedCard>
}