package com.snap2card.feature.snap2card.domain.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OcrTextProcessorTest {
    @Test
    fun `valid OCR text is accepted`() {
        assertTrue(OcrTextProcessor.hasReadableText("Climate change can exacerbate existing inequalities."))
    }

    @Test
    fun `empty OCR result is rejected`() {
        assertFalse(OcrTextProcessor.hasReadableText(""))
    }

    @Test
    fun `whitespace only OCR result is rejected`() {
        assertFalse(OcrTextProcessor.hasReadableText("  \n\t  \n"))
    }

    @Test
    fun `cleanup normalizes blank lines and removes control characters`() {
        val cleaned = OcrTextProcessor.clean("Title\r\n\u0000\u0008Paragraph one.\n\n\n\nParagraph two.\rEnd")

        assertEquals("Title\nParagraph one.\n\nParagraph two.\nEnd", cleaned)
    }
}
