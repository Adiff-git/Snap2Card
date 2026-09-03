package com.snap2card.feature.card_generation.presentation

import androidx.lifecycle.SavedStateHandle
import com.snap2card.feature.card_generation.domain.model.GeneratedVocabularyCard
import com.snap2card.feature.card_generation.domain.repository.GeneratedVocabularyCardStore
import com.snap2card.feature.deck.domain.model.Card
import com.snap2card.feature.deck.domain.model.Deck
import com.snap2card.feature.deck.domain.repository.DeckRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class GeneratedCardsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `stored generated vocabulary cards are exposed to generated cards state`() {
        val viewModel = viewModelWithCards()

        val state = viewModel.uiState.value
        assertTrue(state is GeneratedCardsUiState.Success)
        state as GeneratedCardsUiState.Success
        assertEquals("Scan", state.category)
        assertEquals("exacerbate", state.cards.single().term)
        assertEquals("To make worse.", state.cards.single().definition)
        assertEquals("lam tram trong them", state.cards.single().translation)
        assertTrue(state.cards.single().selected)
        assertFalse(state.canRegenerate)
    }

    @Test
    fun `missing generated cards job returns controlled error`() {
        val viewModel = GeneratedCardsViewModel(
            SavedStateHandle(mapOf("jobId" to "missing-job")),
            GeneratedVocabularyCardStore(),
            FakeDeckRepository(),
        )

        val state = viewModel.uiState.value
        assertTrue(state is GeneratedCardsUiState.Error)
        assertEquals(
            "Generated cards are no longer available. Please scan the image again.",
            (state as GeneratedCardsUiState.Error).message,
        )
    }

    @Test
    fun `select and deselect individual generated card`() {
        val viewModel = viewModelWithCards()
        val cardId = successState(viewModel).cards.single().id

        viewModel.toggleCardSelection(cardId)

        assertFalse(successState(viewModel).cards.single().selected)

        viewModel.toggleCardSelection(cardId)

        assertTrue(successState(viewModel).cards.single().selected)
    }

    @Test
    fun `select all and deselect all generated cards`() {
        val viewModel = viewModelWithCards(cardCount = 2)

        viewModel.deselectAll()
        assertTrue(successState(viewModel).cards.none { it.selected })

        viewModel.selectAll()
        assertTrue(successState(viewModel).cards.all { it.selected })
    }

    @Test
    fun `edit temporary generated card before save`() {
        val viewModel = viewModelWithCards()
        val cardId = successState(viewModel).cards.single().id

        viewModel.updateTerm(cardId, "worsen")
        viewModel.updateDefinition(cardId, "To become worse.")
        viewModel.updateTranslation(cardId, "to become worse")

        val card = successState(viewModel).cards.single()
        assertEquals("worsen", card.term)
        assertEquals("To become worse.", card.definition)
        assertEquals("to become worse", card.translation)
    }

    @Test
    fun `delete generated card removes it only from review session`() {
        val viewModel = viewModelWithCards(cardCount = 2)
        val firstId = successState(viewModel).cards.first().id

        viewModel.deleteCard(firstId)

        val state = successState(viewModel)
        assertEquals(1, state.cards.size)
        assertTrue(state.cards.none { it.id == firstId })
    }

    @Test
    fun `zero selected cards prevents save`() = runTest {
        val repo = FakeDeckRepository()
        val viewModel = viewModelWithCards(repo = repo)
        viewModel.deselectAll()

        viewModel.addSelectedCardsToDeck()
        advanceUntilIdle()

        val state = successState(viewModel)
        assertEquals("Select at least one card to add to a deck.", state.saveError)
        assertEquals(0, repo.createdDecks)
        assertEquals(0, repo.createdCards.size)
    }

    @Test
    fun `invalid selected card prevents save`() = runTest {
        val repo = FakeDeckRepository()
        val viewModel = viewModelWithCards(repo = repo)
        val cardId = successState(viewModel).cards.single().id
        viewModel.updateDefinition(cardId, "")

        viewModel.addSelectedCardsToDeck()
        advanceUntilIdle()

        val state = successState(viewModel)
        assertEquals("Fill in term, definition, and translation for selected cards.", state.saveError)
        assertEquals(0, repo.createdDecks)
        assertEquals(0, repo.createdCards.size)
    }

    @Test
    fun `successful save creates deck and selected cards then marks saved`() = runTest {
        val repo = FakeDeckRepository()
        val viewModel = viewModelWithCards(cardCount = 2, repo = repo)

        viewModel.addSelectedCardsToDeck()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is GeneratedCardsUiState.Saved)
        assertEquals("deck-1", (state as GeneratedCardsUiState.Saved).deckId)
        assertEquals(2, state.savedCount)
        assertEquals(1, repo.createdDecks)
        assertEquals(2, repo.createdCards.size)
    }

    @Test
    fun `save persists only selected edited generated cards`() = runTest {
        val repo = FakeDeckRepository()
        val viewModel = viewModelWithCards(cardCount = 2, repo = repo)
        val firstCard = successState(viewModel).cards.first()
        val secondCard = successState(viewModel).cards.last()
        viewModel.updateDeckName("OCR Deck")
        viewModel.updateTerm(firstCard.id, "worsen")
        viewModel.updateDefinition(firstCard.id, "To make something worse.")
        viewModel.updateTranslation(firstCard.id, "make worse")
        viewModel.toggleCardSelection(secondCard.id)

        viewModel.addSelectedCardsToDeck()
        advanceUntilIdle()

        assertEquals("OCR Deck", repo.lastDeckTitle)
        assertEquals(1, repo.createdCards.size)
        assertEquals("worsen", repo.createdCards.single().front)
        assertEquals(
            "Definition: To make something worse.\nTranslation: make worse",
            repo.createdCards.single().back,
        )
    }

    @Test
    fun `review item builds card back side with metadata`() {
        val card = GeneratedCardReviewItem(
            id = "card-1",
            term = "mitochondria",
            definition = "Produces energy for the cell.",
            translation = "mitochondrion",
            partOfSpeech = "noun",
            example = "Mitochondria generate ATP.",
            sourceSentence = "The mitochondria are organelles.",
            difficulty = "B2",
        )

        assertEquals(
            "Definition: Produces energy for the cell.\n" +
                "Translation: mitochondrion\n" +
                "Part of speech: noun\n" +
                "Example: Mitochondria generate ATP.\n" +
                "Source: The mitochondria are organelles.\n" +
                "Difficulty: B2",
            card.buildBackSide(),
        )
    }

    @Test
    fun `backend save failure keeps review state with controlled error`() = runTest {
        val repo = FakeDeckRepository(failOnAdd = true)
        val viewModel = viewModelWithCards(repo = repo)

        viewModel.addSelectedCardsToDeck()
        advanceUntilIdle()

        val state = successState(viewModel)
        assertFalse(state.isSaving)
        assertEquals("save failed", state.saveError)
        assertEquals(1, repo.createdDecks)
    }

    @Test
    fun `duplicate save click is ignored while saving`() = runTest {
        val repo = FakeDeckRepository(blockAdd = true)
        val viewModel = viewModelWithCards(repo = repo)

        viewModel.addSelectedCardsToDeck()
        viewModel.addSelectedCardsToDeck()
        advanceUntilIdle()

        assertEquals(1, repo.createdDecks)
    }

    private fun viewModelWithCards(
        cardCount: Int = 1,
        repo: FakeDeckRepository = FakeDeckRepository(),
    ): GeneratedCardsViewModel {
        val store = GeneratedVocabularyCardStore()
        val jobId = store.save(
            List(cardCount) { index ->
                GeneratedVocabularyCard(
                    term = if (index == 0) "exacerbate" else "inequality",
                    definition = if (index == 0) "To make worse." else "An unfair difference.",
                    translation = if (index == 0) "lam tram trong them" else "su bat binh dang",
                    partOfSpeech = null,
                    example = null,
                )
            }
        )

        return GeneratedCardsViewModel(SavedStateHandle(mapOf("jobId" to jobId)), store, repo)
    }

    private fun successState(viewModel: GeneratedCardsViewModel): GeneratedCardsUiState.Success {
        val state = viewModel.uiState.value
        assertTrue(state is GeneratedCardsUiState.Success)
        return state as GeneratedCardsUiState.Success
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

private class FakeDeckRepository(
    private val failOnAdd: Boolean = false,
    private val blockAdd: Boolean = false,
) : DeckRepository {
    var createdDecks = 0
    var lastDeckTitle: String? = null
    val createdCards = mutableListOf<Card>()

    override fun getDecks(): Flow<List<Deck>> = flowOf(emptyList())

    override suspend fun getDeckById(deckId: String): Deck? = null

    override suspend fun createDeck(title: String, description: String): Deck {
        createdDecks += 1
        lastDeckTitle = title
        return Deck(
            id = "deck-$createdDecks",
            title = title,
            description = description,
            cardCount = 0,
            createdAt = 0L,
            updatedAt = 0L,
        )
    }

    override suspend fun updateDeck(deck: Deck) = Unit

    override suspend fun deleteDeck(deckId: String) = Unit

    override fun getCardsForDeck(deckId: String): Flow<List<Card>> = flowOf(emptyList())

    override suspend fun addCard(deckId: String, front: String, back: String): Card {
        if (blockAdd) {
            kotlinx.coroutines.delay(1_000)
        }
        if (failOnAdd) error("save failed")
        val card = Card(
            id = "card-${createdCards.size + 1}",
            deckId = deckId,
            front = front,
            back = back,
            createdAt = 0L,
        )
        createdCards += card
        return card
    }

    override suspend fun updateCard(card: Card) = Unit

    override suspend fun deleteCard(cardId: String) = Unit

    override suspend fun addCards(cards: List<Card>) {
        createdCards += cards
    }
}
