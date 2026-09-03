package com.snap2card.feature.account.data.remote

import com.snap2card.feature.account.data.remote.dto.AccountResponse
import retrofit2.http.GET

interface AccountApiService {
    @GET("account")
    suspend fun getAccount(): AccountResponse
}
