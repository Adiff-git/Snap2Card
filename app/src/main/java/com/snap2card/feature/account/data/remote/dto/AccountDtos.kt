package com.snap2card.feature.account.data.remote.dto

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
