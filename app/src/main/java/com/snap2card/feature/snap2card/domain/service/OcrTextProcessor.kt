package com.snap2card.feature.snap2card.domain.service

object OcrTextProcessor {
    const val MIN_OCR_CHARACTERS = 20
    const val NO_READABLE_TEXT_MESSAGE = "No readable text was detected. Try taking a clearer photo."

    fun clean(text: String): String = text
        .replace("\r\n", "\n")
        .replace('\r', '\n')
        .filter { it == '\n' || it == '\t' || it.code >= 32 }
        .lineSequence()
        .map { it.trimEnd() }
        .joinToString("\n")
        .replace(Regex("\n{3,}"), "\n\n")
        .trim()

    fun hasReadableText(text: String): Boolean = clean(text).length >= MIN_OCR_CHARACTERS
}
