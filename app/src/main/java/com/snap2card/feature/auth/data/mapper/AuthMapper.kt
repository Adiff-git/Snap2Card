package com.snap2card.feature.auth.data.mapper

import com.snap2card.feature.auth.data.remote.dto.AuthResponse
import com.snap2card.feature.auth.domain.model.User

fun AuthResponse.toDomain() = User(
    id = userId,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
)
