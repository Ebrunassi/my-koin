package com.study.mykoin.domain.fiis.wallet

import com.study.mykoin.domain.fiis.profile.toUuid
import java.util.*

class WalletId(val value: UUID)  {
    fun stringValue() = value.toString()
    companion object {
        fun new () = UUID.randomUUID().let(::WalletId)

        fun of(stringValue: String) = stringValue.toUuid().map(::WalletId)
    }
}