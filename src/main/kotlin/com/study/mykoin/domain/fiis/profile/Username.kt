package com.study.mykoin.domain.fiis.profile

import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.study.mykoin.domain.fiis.NonEmptyString
import com.study.mykoin.helper.leftIfNull
import com.study.mykoin.usecases.ServiceErrors
import com.study.mykoin.usecases.ServiceErrors.InvalidArgument

@JvmInline
value class Username private constructor(val value: NonEmptyString){

    fun stringValue(): String = value.value
    companion object {
        private const val MIN_USERNAME_LENGTH = 5
        fun of(value: String?) =
            value
                .leftIfNull { InvalidArgument("Username should not be null") }
                .map (NonEmptyString::of)
                .flatMap { it?.right() ?: InvalidArgument("Username should not be blank").left() }
                .flatMap {
                    if (it.value.length < MIN_USERNAME_LENGTH) {
                        InvalidArgument(
                            "Username should contain at least $MIN_USERNAME_LENGTH characters"
                        ).left()
                    } else {
                        it.right()
                    }
                }
                .map(::Username)
    }
}