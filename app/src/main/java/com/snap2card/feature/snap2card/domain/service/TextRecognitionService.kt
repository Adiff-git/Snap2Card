package com.snap2card.feature.snap2card.domain.service

import android.net.Uri
import com.snap2card.feature.snap2card.domain.model.OcrResult

interface TextRecognitionService {
    suspend fun recognizeText(uri: Uri): Result<OcrResult>
    suspend fun recognizePdfText(uri: Uri, maxPages: Int = 10): Result<OcrResult>
}
