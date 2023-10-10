package com.study.mykoin.domain.fiis.fii

import com.study.mykoin.domain.fiis.profile.toUuid
import java.util.*


class FiiId(val value: UUID) {

    fun stringValue() = value.toString()
    companion object {
        fun new () = UUID.randomUUID().let(::FiiId)

        fun of(stringValue: String) = stringValue.toUuid().map(::FiiId)
    }
}