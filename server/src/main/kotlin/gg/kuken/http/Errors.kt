package gg.kuken.http

import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

class HttpException(
    val code: Int,
    message: String?,
    val details: String?,
    val status: HttpStatusCode,
    cause: Throwable?,
) : RuntimeException(message, cause)

@Serializable
data class HttpError(
    val code: Int,
    val message: String,
    val details: String?,
) {
    companion object {
        val InvalidAccessToken = error(2001, "Invalid or missing access token")
        val InsufficientPermissions = error(2002, "Insufficient permissions to access this resource")

        val NotFound = error(0, "Not Found")
        val UnknownAccount = error(1001, "Unknown account")
        val UnknownUnit = error(1002, "Unknown unit")
        val UnknownBlueprint = error(1011, "Unknown blueprint")

        val FailedToParseRequestBody = error(3002, "Failed to handle request")

        fun error(
            code: Int,
            message: String,
            details: String? = null,
        ) = HttpError(code, message, details)
    }
}
