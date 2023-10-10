package com.study.mykoin.helper

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.study.mykoin.usecases.ServiceErrors
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitBody

class Functions

/**
 * Runs this function if the receiver is null
 */
fun <T> T?.otherwise(fn: () -> T): T {
    return this ?: fn()
}

suspend inline fun <reified T: Any> ServerRequest.eitherBody() : Either<ServiceErrors, T> {
    return Either.catch { awaitBody<T>() }
        .mapLeft { ServiceErrors.InvalidRequestBody("Invalid request body") }
}

fun <L, T: Any?> T?.leftIfNull( fn: () -> L): Either<L, T & Any> =
    this?.right() ?: fn().left()