package com.snap2card.feature.deck.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryListResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: CategoryListData,
)

@Serializable
data class CategoryListData(
    @SerialName("categoryNum") val categoryNum: Int? = null,
    @SerialName("categories") val categories: List<CategoryDto> = emptyList(),
)

@Serializable
data class CategoryDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("numOfCard") val numOfCard: Int? = null,
    @SerialName("createdAt") val createdAt: ApiTimeDto? = null,
)

@Serializable
data class CategoryRetrieveRequest(
    @SerialName("id") val id: String,
)

@Serializable
data class CategoryRetrieveResponse(
    @SerialName("status") val status: String? = null,
    @SerialName("data") val data: CategoryRetrieveData,
)

@Serializable
data class CategoryRetrieveData(
    @SerialName("name") val name: String,
    @SerialName("numOfCard") val numOfCard: Int? = null,
    @SerialName("createdAt") val createdAt: ApiTimeDto? = null,
    @SerialName("cardIds") val cardIds: List<String> = emptyList(),
)

@Serializable
data class CategoryDeleteResponse(
    @SerialName("status") val status: String? = null,
)

@Serializable
data class CategoryLogsData(
    val logs: List<CategoryLogDto>,
)

@Serializable
data class CategoryLogDto(
    @SerialName("logId") val logId: String,
    @SerialName("examName") val examName: String,
    @SerialName("score") val score: Int? = null,
    @SerialName("totalScore") val totalScore: Int? = null,
    @SerialName("start") val start: ApiTimeDto? = null,
    @SerialName("end") val end: ApiTimeDto? = null,
)
@Serializable
data class ApiTimeDto(
    @SerialName("year") val year: Int? = null,
    @SerialName("month") val month: Int? = null,
    @SerialName("day") val day: Int? = null,
    @SerialName("hour") val hour: Int? = null,
    @SerialName("minute") val minute: Int? = null,
    @SerialName("second") val second: Int? = null,
    @SerialName("gmt") val gmt: String? = null,
)
