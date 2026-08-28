package com.snap2card.feature.snap2card.data.mapper

import com.snap2card.feature.snap2card.data.remote.dto.GeneratedCardDto
import com.snap2card.feature.snap2card.domain.model.GeneratedCard

fun GeneratedCardDto.toDomain() = GeneratedCard(front = front, back = back)
fun List<GeneratedCardDto>.toDomain() = map { it.toDomain() }
