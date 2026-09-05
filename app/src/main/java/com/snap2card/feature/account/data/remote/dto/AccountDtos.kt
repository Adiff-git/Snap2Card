package com.snap2card.feature.account.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountResponse(
    val data: AccountData
)

@Serializable
data class AccountData(
    val email: String,
    val name: String,
    val phone: String? = null
)

// ASSUMPTION: verify against monthly-learned-count.md. Unknown: is this a
// sparse list (only days with count > 0) or a 0-filled list for every day
// of the month? Affects whether HistoryMapper needs to backfill missing
// days itself for the heatmap grid.
@Serializable
data class MonthlyLearnedCountData(
    val year: Int,
    val month: Int,
    val daily: List<DailyLearnedCountDto>,
)

@Serializable
data class DailyLearnedCountDto(
    @SerialName("day") val day: String,       // doc says "YYYY-MM-DD"; live response is a full ISO
    // timestamp instead — see comment in mapper
    @SerialName("cardCount") val cardCount: Int? = null,
)
