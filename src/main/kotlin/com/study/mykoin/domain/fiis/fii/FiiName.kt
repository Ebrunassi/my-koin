package com.study.mykoin.domain.fiis.fii

import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.study.mykoin.domain.fiis.NonEmptyString
import com.study.mykoin.helper.leftIfNull
import com.study.mykoin.usecases.ServiceErrors
import com.study.mykoin.usecases.ServiceErrors.InvalidArgument

@JvmInline
value class FiiName(val value: NonEmptyString) {
    fun stringValue() = this.value.value

    companion object {
        fun of(value: String?) =
            value
                .leftIfNull { InvalidArgument("Fii name must not be null") }
                .map { it.uppercase() }
                .map (NonEmptyString::of)
                .flatMap {
                    it?.right() ?: InvalidArgument("Fii name must not be blank").left()
                }
                .map (::FiiName)
    }
}