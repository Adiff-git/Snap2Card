package com.snap2card.feature.deck.data.remote

import com.snap2card.feature.deck.data.remote.dto.CategoryListData
import com.snap2card.feature.deck.data.remote.dto.CategoryLogDto
import com.snap2card.feature.deck.data.remote.dto.CategoryLogsData
import com.snap2card.feature.study.data.remote.dto.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface CategoryApiService {

    @Headers("Content-Type: application/json")
    @GET("categories/list")
    suspend fun getCategories(): ApiResponse<CategoryListData>

    @Headers("Content-Type: application/json")
    @GET("categories/logs")
    suspend fun getCategoryLogs(@Query("categoryId") categoryId: String): ApiResponse<List<CategoryLogDto>>

    // Add create/edit/delete/retrieve/categorize methods here as you build out
    // the rest of deck/category — this is just the slice History needs right now.
}