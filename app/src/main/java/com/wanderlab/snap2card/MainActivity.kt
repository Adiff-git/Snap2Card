package com.wanderlab.snap2card

import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wanderlab.snap2card.controllers.serverconnection.ServerConnection
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.EditAccountRequest
import com.wanderlab.snap2card.controllers.serverconnection.definitions.EditCardRequest
import com.wanderlab.snap2card.controllers.serverconnection.definitions.EditCategoryRequest
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    companion object {
        private const val BASE_URL = "https://wanderlab2414.online/"
        private const val TEST_PASSWORD = "TestPassword123"
        private const val PHONE = "0943387815"
        private const val AVATAR_PNG_BASE64 =
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="
        private val DOCUMENT_TEXT = "Photosynthesis is the process by which green plants convert sunlight into " +
            "chemical energy stored in glucose. This process requires chlorophyll, water, and carbon dioxide. " +
            "The oxygen gas released during photosynthesis is essential for the survival of most living organisms."
    }

    private lateinit var buttonRun: Button
    private lateinit var scrollView: ScrollView
    private lateinit var output: TextView
    private val pending = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        buttonRun = findViewById(R.id.buttonRun)
        scrollView = findViewById(R.id.scrollView)
        output = findViewById(R.id.textOutput)
        buttonRun.setOnClickListener { runAllTests() }
    }

    private fun runAllTests() {
        buttonRun.isEnabled = false
        Thread {
            try {
                runBlocking {
                    val connection = ServerConnection(BASE_URL)
                val email = "tester.${System.currentTimeMillis()}@example.com"
                val now = LocalDate.now()

                step("=== 1. ACCOUNT WORKFLOW ===")
                result("login (wrong password, expect 401)", connection.login(email, TEST_PASSWORD + "x"))
                result("register", connection.register("Test User", email, PHONE, TEST_PASSWORD))
                result("login", connection.login(email, TEST_PASSWORD))
                result("getAccount", connection.getAccount())
                result("editAccount (name)", connection.editAccount(EditAccountRequest(type = "name", name = "Test User Renamed")))
                result(
                    "editAccount (dailyGoal=20)",
                    connection.editAccount(EditAccountRequest(type = "dailyGoal", dailyGoal = 20))
                )

                val avatarBytes = Base64.decode(AVATAR_PNG_BASE64, Base64.DEFAULT)
                result("updateAvatar (1x1 png)", connection.updateAvatar(avatarBytes, "image/png"))
                when (val r = connection.getAvatar()) {
                    is ApiResult.Success -> append("getAvatar => OK (${r.data.size} bytes)\n")
                    is ApiResult.Error -> append("getAvatar => ERROR ${r.code}: ${r.message}\n")
                }
                result(
                    "getDailyLearnedCount (${now.year}-${now.monthValue}-${now.dayOfMonth})",
                    connection.getDailyLearnedCount(now.year, now.monthValue, now.dayOfMonth)
                )
                result("getMonthlyLearnedCount", connection.getMonthlyLearnedCount())

                step("=== 2. CATEGORY WORKFLOW ===")
                val banking = connection.createCategory("BANKING")
                result("createCategory (BANKING)", banking)
                val travel = connection.createCategory("TRAVEL")
                result("createCategory (TRAVEL)", travel)
                val categoryId = banking.dataOrNull()?.categoryId ?: travel.dataOrNull()?.categoryId
                val travelId = travel.dataOrNull()?.categoryId
                result("listCategories", connection.listCategories())
                if (categoryId != null) {
                    append("Retrieve category id: $categoryId \n")
                    result("retrieveCategory", connection.retrieveCategory(categoryId))
                    result(
                        "editCategory (rename to BANKING_UPDATED)",
                        connection.editCategory(EditCategoryRequest(id = categoryId, name = "BANKING_UPDATED"))
                    )
                } else {
                    append("retrieveCategory/editCategory => SKIPPED (no category id)\n")
                }

                step("=== 3. CARD WORKFLOW ===")
                val card1 = connection.createCard("What is a cold boot?", "Restarting a computer that is already on.")
                result("createCard (1)", card1)
                val card2 = connection.createCard("What is a hot boot?", "Restarting a computer by cutting and restoring power.")
                result("createCard (2)", card2)
                result("createCardFromDocument", connection.createCardFromDocument(DOCUMENT_TEXT))
                result("createCardFromPdf", connection.createCardFromPdf(buildTestPdf()))
                val card1Id = card1.dataOrNull()?.cards?.firstOrNull()?.id
                val card2Id = card2.dataOrNull()?.cards?.firstOrNull()?.id

                result("listCards", connection.listCards())
                result(
                    "retrieveCards (by ids)",
                    connection.retrieveCards(listOfNotNull(card1Id, card2Id))
                )

                if (card1Id != null) {
                    result(
                        "editCard (frontSide)",
                        connection.editCard(EditCardRequest(id = card1Id, frontSide = "What is a cold boot? (updated)"))
                    )
                } else {
                    append("editCard => SKIPPED (no card 1 id)\n")
                }

                step("=== 4. CATEGORIZE CARDS ===")
                if (card2Id != null && categoryId != null) {
                    result(
                        "categorizeCard (card into BANKING + TRAVEL)",
                        connection.categorizeCard(card2Id, listOf(categoryId, travelId).filterNotNull())
                    )
                } else {
                    append("categorizeCard => SKIPPED (no card/category id)\n")
                }
                val cardIds = listOfNotNull(card1Id, card2Id)
                if (categoryId != null && cardIds.isNotEmpty()) {
                    result("categorizeCards (bulk into BANKING)", connection.categorizeCards(categoryId, cardIds))
                } else {
                    append("categorizeCards => SKIPPED\n")
                }

                step("=== 5. EXAM WORKFLOW ===")
                val exam = if (categoryId != null) {
                    connection.createExam(categoryId)
                } else {
                    ApiResult.Error(400, "skipped: no category")
                }
                result("createExam", exam)
                val examId = exam.dataOrNull()?.examId
                result("listExams", connection.listExams())
                val review = if (examId != null) {
                    connection.reviewExam(examId)
                } else {
                    ApiResult.Error(400, "skipped: no exam")
                }
                result("reviewExam", review)
                val quizzes = review.dataOrNull()?.quizzes.orEmpty()
                val start = if (examId != null) {
                    connection.startExam(examId)
                } else {
                    ApiResult.Error(400, "skipped: no exam")
                }
                result("startExam", start)
                val examLogId = start.dataOrNull()?.examLogId
                if (examLogId != null && quizzes.isNotEmpty()) {
                    quizzes.forEachIndexed { index, quiz ->
                        val answer = index % 2 == 0
                        result("saveExamResult (quiz ${index + 1}, answer=$answer)", connection.saveExamResult(examLogId, quiz.quizId, answer))
                    }
                } else {
                    append("saveExamResult => SKIPPED (no log id / no quizzes)\n")
                }
                if (examLogId != null) {
                    result("completeExam", connection.completeExam(examLogId))
                } else {
                    append("completeExam => SKIPPED (no exam log id)\n")
                }
                    result("getRecentCategories (n=5)", connection.getRecentCategories(5))

                step("=== 6. LEARNED COUNTS AFTER EXAM ===")
                result(
                    "getDailyLearnedCount (${now.year}-${now.monthValue}-${now.dayOfMonth})",
                    connection.getDailyLearnedCount(now.year, now.monthValue, now.dayOfMonth)
                )
                result("getMonthlyLearnedCount", connection.getMonthlyLearnedCount())
                if (categoryId != null) {
                    result("getCategoryLogs", connection.getCategoryLogs(categoryId))
                } else {
                    append("getCategoryLogs => SKIPPED\n")
                }

                step("=== 7. CLEANUP WORKFLOW ===")
                if (card1Id != null) {
                    result("deleteCard", connection.deleteCard(card1Id))
                } else {
                    append("deleteCard => SKIPPED\n")
                }
                if (categoryId != null) {
                    result("deleteCategory", connection.deleteCategory(categoryId))
                } else {
                    append("deleteCategory => SKIPPED\n")
                }
                result("logout", connection.logout())
                result("getAccount (after logout, expect client 401)", connection.getAccount())

                append("\n===== ALL TESTS DONE =====")
                }
            } catch (e: Exception) {
                exception("Test workflow interrupted", e)
            } finally {
                runOnUiThread {
                    buttonRun.isEnabled = true
                }
            }
        }.start()
    }

    private fun exception(label: String, e: Exception) {
        pending.append("\n[EXCEPTION] $label\n")
        pending.append("  ${e.javaClass.simpleName}: ${e.message}\n")
        var cause = e.cause
        var depth = 0
        while (cause != null && depth < 3) {
            pending.append("  Caused by ${cause.javaClass.simpleName}: ${cause.message}\n")
            cause = cause.cause
            depth++
        }
        e.stackTrace.take(8).forEach { pending.append("    at $it\n") }
        render()
    }

    private fun step(name: String) {
        pending.append("\n-- $name --\n")
        render()
    }

    private fun result(label: String, apiResult: ApiResult<*>) {
        when (apiResult) {
            is ApiResult.Success -> pending.append("[OK] $label => ${apiResult.data}\n")
            is ApiResult.Error -> pending.append("[ERROR] $label => ${apiResult.code}: ${apiResult.message}\n")
        }
        render()
    }

    private fun append(text: String) {
        pending.append(text)
        render()
    }

    private fun render() {
        runOnUiThread {
            output.text = pending.toString()
            scrollView.post { scrollView.fullScroll(ScrollView.FOCUS_DOWN) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> ApiResult<T>.dataOrNull(): T? = (this as? ApiResult.Success<T>)?.data

    private fun buildTestPdf(contentText: String = "This is a test PDF document for vocabulary extraction."): ByteArray {
        val content = "BT /F1 18 Tf 72 720 Td ($contentText) Tj ET"
        val objects = listOf(
            "<< /Type /Catalog /Pages 2 0 R >>",
            "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
            "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>",
            "<< /Length ${content.length} >>\nstream\n$content\nendstream",
            "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>"
        )
        val sb = StringBuilder("%PDF-1.4\n")
        val offsets = LongArray(objects.size)
        objects.forEachIndexed { index, obj ->
            offsets[index] = sb.length.toLong()
            sb.append("${index + 1} 0 obj\n$obj\nendobj\n")
        }
        val xrefPosition = sb.length.toLong()
        sb.append("xref\n0 ${objects.size + 1}\n0000000000 65535 f \n")
        offsets.forEach { sb.append("%010d 00000 n \n".format(Locale.US, it)) }
        sb.append("trailer\n<< /Size ${objects.size + 1} /Root 1 0 R >>\nstartxref\n$xrefPosition\n%%EOF")
        return sb.toString().toByteArray(Charsets.ISO_8859_1)
    }
}