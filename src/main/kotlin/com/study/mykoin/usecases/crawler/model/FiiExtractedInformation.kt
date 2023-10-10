package com.study.mykoin.usecases.crawler.model

import com.study.mykoin.domain.fiis.fii.LastIncome
import com.study.mykoin.domain.fiis.fii.NextIncome

class FiiExtractedInformation(
    val name: String,
) {
    var lastIncome: LastIncome = LastIncome()
    var nextIncome: NextIncome = NextIncome()
}
