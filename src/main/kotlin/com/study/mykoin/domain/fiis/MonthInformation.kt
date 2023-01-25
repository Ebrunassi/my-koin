package com.study.mykoin.domain.fiis

import com.study.mykoin.core.fiis.model.enums.CurrencyEnum
import kotlinx.serialization.Serializable

@Serializable
class MonthInformation(
    var timeWindow: String? = null,
    var totalInvested: Double = 0.0,
    val currency: CurrencyEnum? = null,
    var monthlyIncome: Double = 0.0,
    var quantity: Int = 0
)
