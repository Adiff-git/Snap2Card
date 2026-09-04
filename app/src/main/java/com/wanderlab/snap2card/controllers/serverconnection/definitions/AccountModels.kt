package com.wanderlab.snap2card.controllers.serverconnection.definitions

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)

data class RegisterResponse(
    val accountId: String
)

data class AccountResponse(
    val email: String,
    val name: String,
    val phone: String,
    val dailyGoal: Int,
    val createdAt: Time
)

data class EditAccountRequest(
    val type: String,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val dailyGoal: Int? = null
)

data class DailyLearnedCountResponse(
    val count: Int
)

data class MonthlyLearnedEntry(
    val day: String,
    val cardCount: Int
)