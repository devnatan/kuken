package gg.kuken.http.util

import gg.kuken.http.HttpError
import gg.kuken.http.HttpException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveNullable
import jakarta.validation.Validator
import org.koin.ktor.ext.get

suspend inline fun <reified T : Any> ApplicationCall.receiveValid(): T = receiveValidating(get<Validator>())

suspend inline fun <reified T : Any> ApplicationCall.receiveValidating(validator: Validator): T =
    receiveNullable<T>()?.also(validator::validateOrThrow).let(::requireNotNull)

fun respondError(
    error: HttpError,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
    cause: Throwable? = null,
): Nothing = throw HttpException(error.code, error.message, error.details, status, cause)
