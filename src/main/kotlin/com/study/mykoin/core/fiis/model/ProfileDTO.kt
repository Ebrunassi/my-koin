package com.study.mykoin.core.fiis.model

import com.study.mykoin.domain.fiis.MonthIncome

data class ProfileDTO(
    val username: String,
    var password: String,
    var fiiWallet: Collection<String>?,
    var monthIncome: Collection<MonthIncome>?,
    var totalInvested: Double?
)
