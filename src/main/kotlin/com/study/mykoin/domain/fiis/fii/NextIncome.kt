package com.study.mykoin.domain.fiis.fii

import kotlinx.serialization.Serializable

@Serializable
class NextIncome(
    var value: Double? = null,
    var yield: Double? = null,
    var baseValue: Double? = null,
    var baseDay: String? = null,
    var payDay: String? = null
)