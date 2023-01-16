package com.study.mykoin.core.crawler.model

import kotlinx.serialization.Serializable

@Serializable
class LastIncome(
    var value: Double? = null,
    var yield: Double? = null,
    var baseValue: Double? = null,
    var baseDay: String? = null,
    var payDay: String? = null
)
