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
    @SerialName("categoryNum") val categoryNum: Int,
    @SerialName("categories") val categories: List<CategoryDto>,
)

@Serializable
data class CategoryDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("createdAt") val createdAt: ApiTimeDto? = null,
)

@Serializable
data class ApiTimeDto(
    @SerialName("year") val year: Int,
    @SerialName("month") val month: Int,
    @SerialName("day") val day: Int,
    @SerialName("hour") val hour: Int,
    @SerialName("minute") val minute: Int,
    @SerialName("second") val second: Int,
    @SerialName("gmt") val gmt: String,
)
