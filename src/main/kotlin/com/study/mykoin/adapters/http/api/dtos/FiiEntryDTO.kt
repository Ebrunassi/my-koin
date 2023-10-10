package com.study.mykoin.adapters.http.api.dtos

data class FiiEntryDTO(
    val name: String,
    val userId: String,
    val walletId: String,
    val quantity: Int,
    val price: Double,
    var transactionDate: String?, // TODO - Create an object to hold this value and also implement logic on it
    var totalInvested: Double,
    val type: String
    // TODO - Probably we will need to add a field called "taxes",
)
