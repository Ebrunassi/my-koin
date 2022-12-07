package com.study.mykoin.core.common.errors

import kotlinx.serialization.Serializable
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.util.*


/**
 * errorResponse is the function that will hold all error details and will return it to the user.
 * The object that will be returned is ServerResponse, so we build that through this function.
 *
 * badRequestResponse -> this function calls errorResponse and provides all the necessary information
 */

@Serializable
data class Errors(val errors: List<Error>)

@Serializable
data class Error(val id: String, val status: String, val title: String, val detail: String?)
suspend fun errorResponse(
    status: HttpStatus,
    message: String = status.reasonPhrase,
    details: String? = null,
    id: String = UUID.randomUUID().toString()
) = ServerResponse
    .status(status)
    .bodyValueAndAwait(
        Errors(listOf(Error(id = id, status = status.value().toString(), title = message, detail = details)))
    )

suspend fun badRequestResponse(
    details: String? = null,
    id: String = UUID.randomUUID().toString()
) = errorResponse(status = HttpStatus.BAD_REQUEST, details = details, id = id)