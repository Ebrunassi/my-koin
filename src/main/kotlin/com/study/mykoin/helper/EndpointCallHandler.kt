package com.study.mykoin.helper

import arrow.core.Either
import arrow.core.right
import com.study.mykoin.core.common.errors.ServiceErrors
import com.study.mykoin.core.common.errors.badRequestResponse
import com.study.mykoin.core.common.errors.errorResponse
import com.study.mykoin.core.common.response.ServiceResponse
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.web.ErrorResponse
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

suspend fun <L,R> Either<L, R>.handleCall(): ServerResponse {
    return this.fold(
        ifRight = { response ->
            when(response) {
                is ServiceResponse.HelloMyKoin -> ServerResponse.ok().bodyValueAndAwait(response.greetings)
                is ServiceResponse.EventSubmited -> ServerResponse.status(response.statusCode).bodyValueAndAwait(response.message)
                else -> ServerResponse.status(HttpStatus.I_AM_A_TEAPOT).bodyValueAndAwait("This response will no longer exists in a new future")
            }
        },
        ifLeft = { error ->
            when(error) {
                is ServiceErrors.BadRequest -> badRequestResponse(error.reason)
                else -> errorResponse(HttpStatus.I_AM_A_TEAPOT, "This error will no longer exists in a new future")
            }
        }
    )
}