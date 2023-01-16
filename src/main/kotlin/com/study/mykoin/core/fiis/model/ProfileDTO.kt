package com.study.mykoin.core.fiis.model

data class ProfileDTO(
    val username: String,
    var password: String,
    var fiiWallet: Collection<String>?,
    var monthlyIncome: Double?,
    var totalInvested: Double?
)
