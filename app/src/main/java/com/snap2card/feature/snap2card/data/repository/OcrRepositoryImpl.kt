package com.snap2card.feature.snap2card.data.repository

import android.content.Context
import android.net.Uri
import com.snap2card.core.util.FileUtil
import com.snap2card.feature.snap2card.data.mapper.toDomain
import com.snap2card.feature.snap2card.data.remote.OcrApiService
import com.snap2card.feature.snap2card.domain.model.GeneratedCard
import com.snap2card.feature.snap2card.domain.repository.OcrRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OcrRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ocrApiService: OcrApiService,
) : OcrRepository {

    override suspend fun uploadAndProcess(uri: Uri, mimeType: String): Result<List<GeneratedCard>> =
        runCatching {
            val multipart = FileUtil.uriToMultipart(context, uri)
            val response = ocrApiService.uploadFile(multipart)
            response.cards.toDomain()
        }
}
