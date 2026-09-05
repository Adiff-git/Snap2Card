package com.snap2card.feature.home.data.remote.dto

import kotlinx.serialization.Serializable

// ── Time DTO ────────────────────────────────────────────────────────────────

@Serializable
data class TimeDto(
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
    val gmt: String = ""
)

// ── Account (GET /account) ──────────────────────────────────────────────────
// Backend shape:
//   { status:"success", data:{ email, name, phone, dailyGoal, createdAt } }

@Serializable
data class AccountDto(
    val email: String = "",
    val name: String = "",
    val phone: String? = null,          // phone can be null if not set
    val dailyGoal: Int = 20,            // camelCase – matches backend exactly
    val createdAt: TimeDto = TimeDto()
)

@Serializable
data class AccountResponse(
    val status: String = "",
    val data: AccountDto = AccountDto()
)

// ── Recent Categories (GET /categories/recent) ──────────────────────────────
// Backend shape:
//   { status:"success", data: [ { categoryId, name, mastery:number|null, lastTakenAt } ] }

@Serializable
data class RecentCategoryItemDto(
    val categoryId: String = "",        // camelCase – matches backend exactly
    val name: String = "",
    val mastery: Double? = null,        // number|null; use Double to avoid Float cast issues
    val lastTakenAt: TimeDto = TimeDto()
)

@Serializable
data class RecentCategoriesResponse(
    val status: String = "",
    val data: List<RecentCategoryItemDto> = emptyList()
)

// ── Daily Learned Count (GET /account/daily-learned-count) ──────────────────
// Backend shape: { status:"success", data:{ count:number } }

@Serializable
data class DailyLearnedCountData(
    val count: Int = 0
)

@Serializable
data class DailyLearnedCountResponse(
    val status: String = "",
    val data: DailyLearnedCountData = DailyLearnedCountData()
)

// ── Monthly Learned Count (GET /account/monthly-learned-count) ──────────────
// Backend shape: { status:"success", data: [ { day:string, cardCount:number } ] }

@Serializable
data class MonthlyLearnedCountItemDto(
    val day: String = "",               // ISO date "2024-10-15"
    val cardCount: Int = 0              // camelCase – matches backend exactly
)

@Serializable
data class MonthlyLearnedCountResponse(
    val status: String = "",
    val data: List<MonthlyLearnedCountItemDto> = emptyList()
)
