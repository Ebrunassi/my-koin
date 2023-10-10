package com.study.mykoin.domain.fiis

class NonEmptyString (val value: String) {

    fun stringValue() = value
    fun size() = value.length
    companion object {
        fun of(value: String?): NonEmptyString? =
            value?.takeIf (String::isNotBlank)?.let(::NonEmptyString)
    }
}