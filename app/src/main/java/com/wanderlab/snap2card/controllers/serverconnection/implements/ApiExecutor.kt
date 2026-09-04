package com.wanderlab.snap2card.controllers.serverconnection.implements

import com.wanderlab.snap2card.controllers.serverconnection.ApiEndpointConfig
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiError
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiErrorCode
import com.wanderlab.snap2card.controllers.serverconnection.definitions.ApiResult
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CardInfo
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CategorySummary
import com.wanderlab.snap2card.controllers.serverconnection.definitions.CreateCardsResponse
import com.wanderlab.snap2card.controllers.serverconnection.definitions.QuizInfo
import com.wanderlab.snap2card.controllers.serverconnection.definitions.Time
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

internal enum class HttpMethod {
    GET, POST, PUT, DELETE
}

internal const val CT_PDF = ApiEndpointConfig.ContentTypes.PDF

internal class ApiExecutor(
    private val baseUrl: String,
    timeoutSeconds: Long
) {

    companion object {
        private const val KEY_STATUS = "status"
        private const val KEY_MESSAGE = "message"

        private val JSON_MEDIA_TYPE = ApiEndpointConfig.ContentTypes.JSON.toMediaType()
        private val TEXT_MEDIA_TYPE = ApiEndpointConfig.ContentTypes.TEXT.toMediaType()
    }

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
        .build()

    @Volatile
    var token: String? = null

    val isAuthenticated: Boolean
        get() = !token.isNullOrEmpty()

    fun jsonBody(block: JSONObject.() -> Unit): RequestBody =
        JSONObject().apply(block).toString().toRequestBody(JSON_MEDIA_TYPE)

    fun textBody(text: String): RequestBody =
        text.toRequestBody(TEXT_MEDIA_TYPE)

    fun pdfBody(bytes: ByteArray): RequestBody =
        bytes.toRequestBody(CT_PDF.toMediaType())

    fun bytesBody(bytes: ByteArray, mimeType: String): RequestBody =
        bytes.toRequestBody(mimeType.toMediaTypeOrNull())

    suspend fun <T> execute(
        endpoint: ApiEndpointConfig.Endpoint,
        query: List<Pair<String, String>> = emptyList(),
        body: RequestBody? = null,
        onSuccess: (JSONObject) -> T
    ): ApiResult<T> = perform(buildRequest(endpoint, query, body)) { response ->
        if (response.isSuccessful) {
            val raw = response.body?.string() ?: ""
            val json = if (raw.isBlank()) JSONObject() else JSONObject(raw)
            ApiResult.Success(onSuccess(json))
        } else {
            ApiResult.Error(response.code, extractErrorMessage(response))
        }
    }

    suspend fun <T> executeRaw(
        endpoint: ApiEndpointConfig.Endpoint,
        query: List<Pair<String, String>> = emptyList(),
        onSuccess: (Response) -> T
    ): ApiResult<T> = perform(buildRequest(endpoint, query, null)) { response ->
        if (response.isSuccessful) {
            ApiResult.Success(onSuccess(response))
        } else {
            ApiResult.Error(response.code, extractErrorMessage(response))
        }
    }

    private suspend fun <T> perform(
        request: Request,
        handle: (Response) -> ApiResult<T>
    ): ApiResult<T> = try {
        client.newCall(request).execute().use(handle)
    } catch (e: ApiError) {
        ApiResult.Error(e.code, e.message ?: "Unknown error")
    } catch (e: IOException) {
        ApiResult.Error(ApiErrorCode.INTERNAL_SERVER_ERROR, "Network error: ${e.message}")
    } catch (e: Exception) {
        ApiResult.Error(ApiErrorCode.INTERNAL_SERVER_ERROR, e.message ?: "Unknown error")
    }

    private fun buildRequest(
        endpoint: ApiEndpointConfig.Endpoint,
        query: List<Pair<String, String>>,
        body: RequestBody?
    ): Request {
        val headers = Headers.Builder().apply {
            (endpoint.contentType ?: body?.contentType()?.toString())?.let { add("Content-Type", it) }
            if (endpoint.isAuth) {
                val currentToken = token
                if (currentToken.isNullOrEmpty()) {
                    throw ApiError(ApiErrorCode.UNAUTHORIZED, "Not authenticated")
                }
                add("Authorization", "${ApiEndpointConfig.AUTH_SCHEME} $currentToken")
            }
        }.build()

        val requestBuilder = Request.Builder()
            .url(buildUrl(endpoint.path, query))
            .headers(headers)
            .method(endpoint.method.name, body)
        return requestBuilder.build()
    }

    private fun buildUrl(path: String, query: List<Pair<String, String>>): String {
        val sb = StringBuilder(baseUrl.trimEnd('/'))
            .append(ApiEndpointConfig.BASE_PATH)
            .append(path)
        if (query.isNotEmpty()) {
            sb.append('?')
            query.forEach { (key, value) ->
                sb.append(key).append('=').append(URLEncoder.encode(value, "UTF-8")).append('&')
            }
            sb.setLength(sb.length - 1)
        }
        return sb.toString()
    }

    private fun extractErrorMessage(response: Response): String = try {
        val raw = response.body?.string() ?: ""
        if (raw.isBlank()) {
            "HTTP ${response.code}"
        } else {
            val json = JSONObject(raw)
            if (json.optString(KEY_STATUS) == "error") {
                json.optString(KEY_MESSAGE, "HTTP ${response.code}")
            } else {
                "HTTP ${response.code}"
            }
        }
    } catch (_: Exception) {
        "HTTP ${response.code}"
    }

    }

// ---------- JSON converters ----------

internal fun JSONObject.toTime(): Time =
    Time(
        year = getInt("year"),
        month = getInt("month"),
        day = getInt("day"),
        hour = getInt("hour"),
        minute = getInt("minute"),
        second = getInt("second"),
        gmt = getString("gmt")
    )

internal fun JSONObject.toCreateCardsResponse(): CreateCardsResponse =
    CreateCardsResponse(
        numOfCard = getInt("numOfCard"),
        cards = getJSONArray("cards").toCardInfoList()
    )

internal fun JSONObject.toCreateCardsResponse(frontSide: String, backSide: String): CreateCardsResponse =
    CreateCardsResponse(
        numOfCard = getInt("numOfCard"),
        cards = getJSONArray("cards").toCardInfoList(frontSide, backSide)
    )

internal fun JSONArray.toCardInfoList(): List<CardInfo> = buildList {
    for (i in 0 until length()) {
        val item = getJSONObject(i)
        add(
            CardInfo(
                id = if (item.has("id")) item.getString("id") else null,
                frontSide = if (item.has("frontSide")) item.getString("frontSide") else null,
                backSide = if (item.has("backSide")) item.getString("backSide") else null
            )
        )
    }
}

internal fun JSONArray.toCardInfoList(frontSide: String, backSide: String): List<CardInfo> = buildList {
    for (i in 0 until length()) {
        val item = getJSONObject(i)
        add(
            CardInfo(
                id = if (item.has("id")) item.getString("id") else null,
                frontSide,
                backSide
            )
        )
    }
}
internal fun JSONArray.toCategorySummaryList(): List<CategorySummary> = buildList {
    for (i in 0 until length()) {
        val item = getJSONObject(i)
        add(
            CategorySummary(
                id = item.getString("id"),
                name = item.getString("name"),
                numOfCard = item.getInt("numOfCard"),
                mastery = if (item.isNull("mastery")) null else item.optDouble("mastery"),
                createdAt = item.getJSONObject("createdAt").toTime()
            )
        )
    }
}

internal fun JSONArray.toQuizInfoList(): List<QuizInfo> = buildList {
    for (i in 0 until length()) {
        val item = getJSONObject(i)
        add(
            QuizInfo(
                quizId = item.getString("quizId"),
                frontSide = item.getString("frontSide"),
                backSide = item.getString("backSide")
            )
        )
    }
}

internal fun JSONArray.toStringList(): List<String> = buildList {
    for (i in 0 until length()) {
        add(getString(i))
    }
}