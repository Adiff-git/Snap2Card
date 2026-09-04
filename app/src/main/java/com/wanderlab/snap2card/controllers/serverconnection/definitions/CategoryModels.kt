package com.wanderlab.snap2card.controllers.serverconnection.definitions

data class CreateCategoryRequest(
    val name: String
)

data class CreateCategoryResponse(
    val categoryId: String
)

data class EditCategoryRequest(
    val id: String,
    val name: String? = null
)

data class CategorySummary(
    val id: String,
    val name: String,
    val numOfCard: Int,
    val mastery: Double?,
    val createdAt: Time
)

data class CategoryListResponse(
    val categoryNum: Int,
    val categories: List<CategorySummary>
)

data class CategoryDetail(
    val name: String,
    val numOfCard: Int,
    val mastery: Double?,
    val createdAt: Time,
    val cardIds: List<String>
)

data class ExamLogEntry(
    val logId: String,
    val examName: String,
    val score: Int,
    val totalScore: Int,
    val start: Time,
    val end: Time
)

data class RecentCategoryEntry(
    val categoryId: String,
    val name: String,
    val mastery: Double?,
    val lastTakenAt: Time
)

data class CategorizeCardRequest(
    val cardId: String,
    val categoryIds: List<String>
)

data class CategorizeCardsRequest(
    val categoryId: String,
    val cardIds: List<String>
)