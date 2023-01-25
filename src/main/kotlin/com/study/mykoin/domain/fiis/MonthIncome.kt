package com.study.mykoin.domain.fiis

import kotlinx.serialization.Serializable

@Serializable
class MonthIncome(
    var timeWindow: String, // ex: December/2022, January/2023 or 01/2023, 12/2022
    var value: Double? = null,
    var yield: Double? = null,
    var completed: Boolean = false // completed = true means we got information regarding all fiis we have in our wallet
)
