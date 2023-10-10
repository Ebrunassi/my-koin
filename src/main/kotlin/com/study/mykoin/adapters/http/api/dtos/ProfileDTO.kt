package com.study.mykoin.adapters.http.api.dtos

import com.study.mykoin.domain.fiis.MonthIncome

data class ProfileDTO(
    val username: String,
    var password: String,
    var fiiWallet: Collection<String>?,
    var monthIncome: Collection<MonthIncome>?,
    var totalInvested: Double?
)
