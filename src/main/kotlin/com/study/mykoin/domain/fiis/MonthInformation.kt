package com.study.mykoin.domain.fiis

import com.study.mykoin.domain.fiis.enums.CurrencyEnum
import kotlinx.serialization.Serializable

@Serializable
data class MonthInformation(
    var timeWindow: String? = null,
    var totalInvested: Double = 0.0,
    val currency: CurrencyEnum? = null,
    var monthlyIncome: Double = 0.0,
    var quantity: Int = 0
)
