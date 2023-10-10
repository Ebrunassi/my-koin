package com.study.mykoin.domain.fiis.profile

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import com.study.mykoin.usecases.ServiceErrors
import java.util.*

data class ProfileId(val value: UUID) {

    fun stringValue() = value.toString()
    companion object {
        fun new() = UUID.randomUUID().let(::ProfileId)

        fun of(stringValue: String) = stringValue.toUuid().map(::ProfileId)
    }
}

fun String.toUuid(): Either<ServiceErrors, UUID> =
        Either.catch {
            UUID.fromString(this).right()
        }.getOrElse {
            ServiceErrors.InvalidUuid("Invalid Uuid: $this").left()
        }