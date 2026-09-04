package com.wanderlab.snap2card.controllers.serverconnection

import com.wanderlab.snap2card.controllers.serverconnection.implements.HttpMethod

internal object ApiEndpointConfig {

    const val BASE_PATH = "/snap2card/api/v1.0"

    object ContentTypes {
        const val JSON = "application/json; charset=utf-8"
        const val TEXT = "text/plain; charset=utf-8"
        const val PDF = "application/pdf"
        const val IMAGE_PNG = "image/png"
        const val IMAGE_JPEG = "image/jpeg"
        const val IMAGE_WEBP = "image/webp"
        const val IMAGE_BMP = "image/bmp"
        const val IMAGE_X_ICON = "image/x-icon"
    }

    const val AUTH_SCHEME = "Bearer"

    internal data class Endpoint(
        val method: HttpMethod,
        val path: String,
        val isAuth: Boolean,
        val contentType: String?,
        val queryParams: List<String> = emptyList(),
        val description: String
    )

    object Endpoints {

        // ---------- Account ----------

        val ACCOUNT_LOGIN = Endpoint(
            method = HttpMethod.POST,
            path = "/account/login",
            isAuth = false,
            contentType = ContentTypes.JSON,
            description = "Authenticates a user and returns an access token."
        )
        val ACCOUNT_REGISTER = Endpoint(
            method = HttpMethod.POST,
            path = "/account/register",
            isAuth = false,
            contentType = ContentTypes.JSON,
            description = "Creates a new account."
        )
        val ACCOUNT_RETRIEVE = Endpoint(
            method = HttpMethod.GET,
            path = "/account",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Retrieves the authenticated user's account details."
        )
        val ACCOUNT_AVATAR_RETRIEVE = Endpoint(
            method = HttpMethod.GET,
            path = "/account/avatar",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Retrieves the authenticated user's avatar as raw image data."
        )
        val ACCOUNT_AVATAR_UPDATE = Endpoint(
            method = HttpMethod.PUT,
            path = "/account/avatar",
            isAuth = true,
            contentType = null,
            description = "Updates the avatar by uploading an image file (png/jpeg/webp/bmp/x-icon); content type from the uploaded image."
        )
        val ACCOUNT_EDIT = Endpoint(
            method = HttpMethod.PUT,
            path = "/account",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Updates account details by type (total/name/email/phone/dailyGoal)."
        )
        val ACCOUNT_LOGOUT = Endpoint(
            method = HttpMethod.POST,
            path = "/account/logout",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Invalidates the current user's session/token."
        )
        val ACCOUNT_DAILY_LEARNED_COUNT = Endpoint(
            method = HttpMethod.GET,
            path = "/account/daily-learned-count",
            isAuth = true,
            contentType = ContentTypes.JSON,
            queryParams = listOf("year", "month", "day"),
            description = "Returns the number of cards learned on a given date."
        )
        val ACCOUNT_MONTHLY_LEARNED_COUNT = Endpoint(
            method = HttpMethod.GET,
            path = "/account/monthly-learned-count",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Returns the number of cards learned per day this month."
        )

        // ---------- Cards ----------

        val CARD_CREATE = Endpoint(
            method = HttpMethod.POST,
            path = "/cards",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Creates a new card from front/back side text."
        )
        val CARD_CREATE_FROM_DOCUMENT = Endpoint(
            method = HttpMethod.POST,
            path = "/cards/document",
            isAuth = true,
            contentType = ContentTypes.TEXT,
            description = "Creates a new card from a raw text document."
        )
        val CARD_CREATE_FROM_PDF = Endpoint(
            method = HttpMethod.POST,
            path = "/cards/pdf",
            isAuth = true,
            contentType = ContentTypes.PDF,
            description = "Saves a PDF file and records it in the database."
        )
        val CARD_EDIT = Endpoint(
            method = HttpMethod.PUT,
            path = "/cards",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Updates an existing card."
        )
        val CARD_DELETE = Endpoint(
            method = HttpMethod.DELETE,
            path = "/cards",
            isAuth = true,
            contentType = ContentTypes.JSON,
            queryParams = listOf("id"),
            description = "Deletes a card, or un-haves it if not the creator."
        )
        val CARD_LIST = Endpoint(
            method = HttpMethod.GET,
            path = "/cards/list",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Lists the cards for the authenticated user."
        )
        val CARD_RETRIEVE = Endpoint(
            method = HttpMethod.GET,
            path = "/cards",
            isAuth = true,
            contentType = ContentTypes.JSON,
            queryParams = listOf("ids"),
            description = "Retrieves one or more cards by ids (query)."
        )
        val CARD_CATEGORIZE = Endpoint(
            method = HttpMethod.POST,
            path = "/cards/categorize",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Assigns a card to one or more categories."
        )

        // ---------- Categories ----------

        val CATEGORY_CREATE = Endpoint(
            method = HttpMethod.POST,
            path = "/categories",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Creates a new category from a name."
        )
        val CATEGORY_EDIT = Endpoint(
            method = HttpMethod.PUT,
            path = "/categories",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Updates a category name."
        )
        val CATEGORY_DELETE = Endpoint(
            method = HttpMethod.DELETE,
            path = "/categories",
            isAuth = true,
            contentType = ContentTypes.JSON,
            queryParams = listOf("id"),
            description = "Deletes a category, or unfollows it if not the owner."
        )
        val CATEGORY_LIST = Endpoint(
            method = HttpMethod.GET,
            path = "/categories/list",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Lists all categories for the authenticated user."
        )
        val CATEGORY_RETRIEVE = Endpoint(
            method = HttpMethod.GET,
            path = "/categories",
            isAuth = true,
            contentType = ContentTypes.JSON,
            queryParams = listOf("id"),
            description = "Retrieves a single category with its cards."
        )
        val CATEGORY_CATEGORIZE = Endpoint(
            method = HttpMethod.POST,
            path = "/categories/categorize",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Assigns one or more cards to a category."
        )
        val CATEGORY_LOGS = Endpoint(
            method = HttpMethod.GET,
            path = "/categories/logs",
            isAuth = true,
            contentType = ContentTypes.JSON,
            queryParams = listOf("categoryId"),
            description = "Lists completed exam logs for a category (query categoryId)."
        )
        val CATEGORY_RECENT = Endpoint(
            method = HttpMethod.GET,
            path = "/categories/recent",
            isAuth = true,
            contentType = ContentTypes.JSON,
            queryParams = listOf("n"),
            description = "Lists the most recent categories the user took exams in."
        )

        // ---------- Exams ----------

        val EXAM_CREATE = Endpoint(
            method = HttpMethod.POST,
            path = "/exams/create",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Creates a new exam from a category."
        )
        val EXAM_START = Endpoint(
            method = HttpMethod.POST,
            path = "/exams/start",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Starts an exam session and returns a new exam log."
        )
        val EXAM_RESULT = Endpoint(
            method = HttpMethod.POST,
            path = "/exams/result",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Saves a quiz result against an exam log."
        )
        val EXAM_REVIEW = Endpoint(
            method = HttpMethod.GET,
            path = "/exams/review",
            isAuth = true,
            contentType = ContentTypes.JSON,
            queryParams = listOf("examId"),
            description = "Retrieves the quizzes for reviewing an exam (query examId)."
        )
        val EXAM_COMPLETED = Endpoint(
            method = HttpMethod.POST,
            path = "/exams/completed",
            isAuth = true,
            contentType = ContentTypes.JSON,
            description = "Finalizes an exam log and grades the exam."
        )
        val EXAM_LIST = Endpoint(
            method = HttpMethod.GET,
            path = "/exams/list",
            isAuth = true,
            contentType = null,
            description = "Lists the exams available to the authenticated user."
        )

        val ALL: List<Endpoint> = listOf(
            ACCOUNT_LOGIN, ACCOUNT_REGISTER, ACCOUNT_RETRIEVE, ACCOUNT_AVATAR_RETRIEVE,
            ACCOUNT_AVATAR_UPDATE, ACCOUNT_EDIT, ACCOUNT_LOGOUT,
            ACCOUNT_DAILY_LEARNED_COUNT, ACCOUNT_MONTHLY_LEARNED_COUNT,
            CARD_CREATE, CARD_CREATE_FROM_DOCUMENT, CARD_CREATE_FROM_PDF, CARD_EDIT,
            CARD_DELETE, CARD_LIST, CARD_RETRIEVE, CARD_CATEGORIZE,
            CATEGORY_CREATE, CATEGORY_EDIT, CATEGORY_DELETE, CATEGORY_LIST,
            CATEGORY_RETRIEVE, CATEGORY_CATEGORIZE, CATEGORY_LOGS, CATEGORY_RECENT,
            EXAM_CREATE, EXAM_START, EXAM_RESULT, EXAM_REVIEW, EXAM_COMPLETED, EXAM_LIST
        )
    }
}