package com.snap2card.feature.settings.domain.usecase

import com.snap2card.feature.settings.domain.model.UserSettings
import com.snap2card.feature.settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(private val repo: SettingsRepository) {
    operator fun invoke(): Flow<UserSettings> = repo.getSettings()
}

class UpdateSettingsUseCase @Inject constructor(private val repo: SettingsRepository) {
    suspend operator fun invoke(settings: UserSettings) = repo.updateSettings(settings)
}
