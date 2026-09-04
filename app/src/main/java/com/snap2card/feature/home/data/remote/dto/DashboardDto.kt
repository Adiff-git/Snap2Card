package com.snap2card.feature.home.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Time DTO (used across many backend responses) ──────────────────────────

@Serializable
data class TimeDto(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Int,
    val gmt: String
)

// ── Account (GET /account) ─────────────────────────────────────────────────

@Serializable
data class AccountDto(
    val email: String,
    val name: String,
    val phone: String,
    @SerialName("dailyGoal") val dailyGoal: Int,
    val createdAt: TimeDto
)

@Serializable
data class AccountResponse(
    val status: String,
    val data: AccountDto
)

// ── Recent Categories (GET /categories/recent) ─────────────────────────────

@Serializable
data class RecentCategoryItemDto(
    @SerialName("categoryId") val categoryId: String,
    val name: String,
    val mastery: Float? = null,
    @SerialName("lastTakenAt") val lastTakenAt: TimeDto
)

@Serializable
data class RecentCategoriesResponse(
    val status: String,
    val data: List<RecentCategoryItemDto>
)

// ── Daily Learned Count (GET /account/daily-learned-count) ─────────────────

@Serializable
data class DailyLearnedCountData(
    val count: Int
)

@Serializable
data class DailyLearnedCountResponse(
    val status: String,
    val data: DailyLearnedCountData
)

// ── Monthly Learned Count (GET /account/monthly-learned-count) ─────────────

@Serializable
data class MonthlyLearnedCountItemDto(
    val day: String,         // ISO date, e.g. "2024-10-15"
    @SerialName("cardCount") val cardCount: Int
)

@Serializable
data class MonthlyLearnedCountResponse(
    val status: String,
    val data: List<MonthlyLearnedCountItemDto>
)
