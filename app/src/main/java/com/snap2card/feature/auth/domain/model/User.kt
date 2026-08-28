package com.snap2card.feature.auth.domain.model

/** Domain entity representing the authenticated user. */
data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String?,
)
