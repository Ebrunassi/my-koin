package com.study.mykoin.core.fiis.model

import java.util.Date

data class FiiDTO(
    val name: String,
    val quantity: Int,
    val averagePrice: Double,
    var transactionDate: String?,       // TODO - Create an object to hold this value and also implement logic on it
    var totalInvested: Double,
    val type: String
    // TODO - Probably we will need to add a field called "taxes",
    )