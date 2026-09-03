package com.snap2card.feature.card_generation.data.mapper

import com.snap2card.feature.card_generation.data.remote.VocabularyApiService
import com.snap2card.feature.card_generation.data.remote.dto.GeneratedVocabularyCardDto
import com.snap2card.feature.card_generation.data.remote.dto.VocabularyFromTextRequest
import com.snap2card.feature.card_generation.data.remote.dto.VocabularyGenerationData
import com.snap2card.feature.card_generation.data.remote.dto.VocabularyGenerationResponse
import com.snap2card.feature.card_generation.data.remote.dto.VocabularyGenerationSourceDto
import com.snap2card.feature.card_generation.data.repository.VocabularyGenerationRepositoryImpl
import com.snap2card.feature.card_generation.domain.model.GeneratedVocabularyCard
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VocabularyMapperTest {
    @Test
    fun `parses vocabulary response dto`() {
        val json = Json { ignoreUnknownKeys = true }
        val response = json.decodeFromString<VocabularyGenerationResponse>(
            """
            {
              "status": "success",
              "data": {
                "source": { "type": "scan" },
                "cards": [
                  {
                    "term": "exacerbate",
                    "definition": "To make worse.",
                    "translation": "lam tram trong them",
                    "partOfSpeech": "verb"
                  }
                ]
              }
            }
            """.trimIndent()
        )

        assertEquals("success", response.status)
        assertEquals("scan", response.data.source.type)
        assertEquals("exacerbate", response.data.cards.first().term)
    }

    @Test
    fun `maps generated vocabulary card dto to domain`() {
        val domain = GeneratedVocabularyCardDto(
            term = "inequality",
            definition = "An unfair difference.",
            translation = "su bat binh dang",
            difficulty = "B1",
        ).toDomain()

        assertEquals("inequality", domain.term)
        assertEquals("An unfair difference.", domain.definition)
        assertEquals("su bat binh dang", domain.translation)
        assertEquals("B1", domain.difficulty)
    }

    @Test
    fun `maps generated vocabulary card to card create request`() {
        val request = GeneratedVocabularyCard(
            term = "vulnerable",
            definition = "Easily harmed.",
            translation = "de bi ton thuong",
            partOfSpeech = "adjective",
            example = "Some groups are vulnerable.",
        ).toCardCreateRequest()

        assertEquals("manual", request.type)
        assertEquals("vulnerable", request.frontSide)
        assertTrue(request.backSide.contains("Definition: Easily harmed."))
        assertTrue(request.backSide.contains("Translation: de bi ton thuong"))
        assertTrue(request.backSide.contains("Part of speech: adjective"))
    }

    @Test
    fun `repository returns mapped mock response`() = runBlocking {
        val repository = VocabularyGenerationRepositoryImpl(FakeVocabularyApiService())

        val result = repository.generateVocabularyFromText(
            text = "Climate change can exacerbate inequalities.",
            level = "B1",
            count = 20,
            includePhrases = true,
            sourceType = "scan",
        )

        assertTrue(result.isSuccess)
        assertEquals("exacerbate", result.getOrThrow().first().term)
    }

    private class FakeVocabularyApiService : VocabularyApiService {
        override suspend fun generateVocabularyFromText(
            request: VocabularyFromTextRequest,
        ): VocabularyGenerationResponse = VocabularyGenerationResponse(
            status = "success",
            data = VocabularyGenerationData(
                source = VocabularyGenerationSourceDto(type = request.sourceType),
                cards = listOf(
                    GeneratedVocabularyCardDto(
                        term = "exacerbate",
                        definition = "To make worse.",
                        translation = "lam tram trong them",
                    )
                ),
            ),
        )
    }
}
