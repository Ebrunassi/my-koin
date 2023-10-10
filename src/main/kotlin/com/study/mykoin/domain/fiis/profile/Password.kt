package com.study.mykoin.domain.fiis.profile

import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.study.mykoin.domain.fiis.NonEmptyString
import com.study.mykoin.helper.leftIfNull
import com.study.mykoin.usecases.ServiceErrors
import com.study.mykoin.usecases.ServiceErrors.InvalidArgument

@JvmInline
value class Password private constructor(val value: NonEmptyString){

    fun stringValue() = value.value

    companion object {

        private const val PASSWORD_MIN_LENGTH = 5
        fun of(value: String?) =
            value
                .leftIfNull { InvalidArgument("Password should be present") }
                .map (NonEmptyString::of)
                .flatMap { it?.right() ?: InvalidArgument("Password should not be blank").left() }
                .flatMap {
                    if (it.size() < PASSWORD_MIN_LENGTH) {
                        InvalidArgument("Password should be grater than $PASSWORD_MIN_LENGTH characters").left()
                    } else {
                        it.right()
                    }
                }
                .map(::Password)
    }
}