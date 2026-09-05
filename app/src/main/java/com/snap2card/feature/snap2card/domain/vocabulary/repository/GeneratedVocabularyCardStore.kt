package com.snap2card.feature.snap2card.domain.vocabulary.repository

import com.snap2card.feature.snap2card.domain.vocabulary.model.GeneratedVocabularyCard
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneratedVocabularyCardStore @Inject constructor() {
    private val cardsByJobId = ConcurrentHashMap<String, List<GeneratedVocabularyCard>>()

    fun save(cards: List<GeneratedVocabularyCard>): String {
        val jobId = UUID.randomUUID().toString()
        cardsByJobId[jobId] = cards
        return jobId
    }

    fun get(jobId: String): List<GeneratedVocabularyCard>? = cardsByJobId[jobId]
}
